from flask import Flask, request, jsonify
from flask_cors import CORS
import cv2
import numpy as np
import pytesseract
import imutils
import logging
import os

app = Flask(__name__)
CORS(app)  # Enable CORS for cross-origin requests

# Configure logging
logging.basicConfig(level=logging.DEBUG)

# Create a directory to save intermediate images if it doesn't exist
os.makedirs('/home/keith-karugu/Documents/intermediate_steps', exist_ok=True)

def is_valid_aspect_ratio(width, height):
    aspect_ratio = width / float(height)
    return (2 <= aspect_ratio <= 5) or (0.4 <= aspect_ratio <= 0.6)  # Adjusted for both plate dimensions

@app.route('/analyze', methods=['POST'])
def analyze_plate():
    logging.debug("Request received for analyze_plate")

    if 'image' not in request.files:
        logging.debug("No image provided")
        return jsonify({'error': 'No image provided'}), 400

    file = request.files['image']
    npimg = np.frombuffer(file.read(), np.uint8)
    img = cv2.imdecode(npimg, cv2.IMREAD_COLOR)
    logging.debug(f"Image shape: {img.shape}")

    # Save the uploaded image for debugging purposes
    cv2.imwrite('/home/keith-karugu/Documents/intermediate_steps/uploaded_image.jpg', img)
    logging.debug("Uploaded image saved for debugging")

    # Convert image to grayscale
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    cv2.imwrite('/home/keith-karugu/Documents/intermediate_steps/gray_image.jpg', gray)
    
    bfilter = cv2.bilateralFilter(gray, 11, 17, 17)  # Noise reduction
    cv2.imwrite('/home/keith-karugu/Documents/intermediate_steps/bfilter_image.jpg', bfilter)

    edged = cv2.Canny(bfilter, 30, 200)  # Edge detection
    cv2.imwrite('/home/keith-karugu/Documents/intermediate_steps/edged_image.jpg', edged)
    logging.debug("Edge detection completed")

    keypoints = cv2.findContours(edged.copy(), cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    contours = imutils.grab_contours(keypoints)  # Use imutils.grab_contours
    contours = sorted(contours, key=cv2.contourArea, reverse=True)[:20]  # Sort contours
    logging.debug(f"Number of contours found: {len(contours)}")

    location = None
    for contour in contours:
        approx = cv2.approxPolyDP(contour, 0.02 * cv2.arcLength(contour, True), True)
        logging.debug(f"Contour points: {len(approx)}, Contour: {approx}")
        
        x, y, width, height = cv2.boundingRect(approx)
        if is_valid_aspect_ratio(width, height) and (4 <= len(approx) <= 6):  # Allow 4 to 6 points for slight curvature
            location = approx
            break

    logging.debug(f"Contour location: {location}")

    if location is None:
        logging.debug("License plate not found")
        return jsonify({'error': 'License plate not found'}), 404

    # Applying perspective transformation to correct for curvature and positioning
    rect = cv2.minAreaRect(location)
    box = cv2.boxPoints(rect)
    box = np.int0(box)
    width = int(rect[1][0])
    height = int(rect[1][1])

    src_pts = box.astype("float32")
    dst_pts = np.array([[0, height - 1],
                        [0, 0],
                        [width - 1, 0],
                        [width - 1, height - 1]], dtype="float32")
    M = cv2.getPerspectiveTransform(src_pts, dst_pts)
    warped = cv2.warpPerspective(gray, M, (width, height))

    cv2.imwrite('/home/keith-karugu/Documents/intermediate_steps/warped_image.jpg', warped)
    logging.debug(f"Warped image shape: {warped.shape}")

    # Additional pre-processing for better OCR accuracy
    warped = cv2.threshold(warped, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)[1]
    
    # Morphological Transformations
    kernel = np.ones((3, 3), np.uint8)
    warped = cv2.morphologyEx(warped, cv2.MORPH_CLOSE, kernel)
    warped = cv2.medianBlur(warped, 3)
    cv2.imwrite('/home/keith-karugu/Documents/intermediate_steps/processed_warped_image.jpg', warped)

    # Configuring Tesseract OCR
    custom_config = r'--oem 3 --psm 7'
    text = pytesseract.image_to_string(warped, config=custom_config)
    logging.debug(f"Detected plate text: {text.strip()}")

    return jsonify({'plate_text': text.strip()})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
