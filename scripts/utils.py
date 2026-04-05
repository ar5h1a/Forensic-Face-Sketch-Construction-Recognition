
import cv2
import numpy as np
from skimage.feature import hog


def preprocess_image(image_path):
    img = cv2.imread(image_path)

    if img is None:
        raise ValueError(f"Image not found: {image_path}")

    img = cv2.resize(img, (256, 256))

    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # 🔥 Improve contrast (IMPORTANT for sketches)
    gray = cv2.equalizeHist(gray)

    # 🔥 Edge enhancement
    edges = cv2.Canny(gray, 50, 150)

    # Blend edges + original
    gray = cv2.addWeighted(gray, 0.7, edges, 0.3, 0)

    # Slight blur
    gray = cv2.GaussianBlur(gray, (3, 3), 0)

    return gray


def extract_features(image):
    features1 = hog(image, orientations=9, pixels_per_cell=(16,16),
                    cells_per_block=(2,2), block_norm='L2-Hys')

    small = cv2.resize(image, (128, 128))

    features2 = hog(small, orientations=9, pixels_per_cell=(8,8),
                    cells_per_block=(2,2), block_norm='L2-Hys')

    features = np.concatenate([features1, features2])

    norm = np.linalg.norm(features)
    if norm != 0:
        features = features / norm

    return features


# Cosine similarity (safe version)
def cosine_similarity(a, b):
    return float(np.dot(a, b))


