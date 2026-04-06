
# import os
# import numpy as np
# from scripts.utils import preprocess_image, extract_features

# IMAGE_FOLDER = "../database/images"
# SAVE_PATH = "../embeddings/photo_embeddings.npy"

# embeddings = []
# filenames = []

# for file in os.listdir(IMAGE_FOLDER):
#     if not (file.endswith(".jpg") or file.endswith(".png")):
#         continue

#     path = os.path.join(IMAGE_FOLDER, file)

#     try:
#         img = preprocess_image(path)
#         features = extract_features(img)

#         embeddings.append(features)
#         filenames.append(file)

#         print(f"✅ Processed: {file}")

#     except Exception as e:
#         print(f"❌ Skipped {file}: {e}")

# # Convert to numpy
# embeddings = np.array(embeddings)

# # Save
# np.save(SAVE_PATH, {
#     "embeddings": embeddings,
#     "filenames": filenames
# })

# print("\n🔥 Embeddings built successfully!")
# print(f"Total images processed: {len(filenames)}")

import os
import numpy as np
from scripts.utils import preprocess_image, extract_features

# 🔥 Base directory
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# 🔥 Paths
IMAGE_FOLDER = os.path.join(BASE_DIR, "database", "images")
SAVE_PATH = os.path.join(BASE_DIR, "embeddings", "photo_embeddings.npy")

embeddings = []
filenames = []

# 🔥 Process all images
for file in os.listdir(IMAGE_FOLDER):
    if not (file.endswith(".jpg") or file.endswith(".png")):
        continue

    path = os.path.join(IMAGE_FOLDER, file)

    try:
        img = preprocess_image(path)
        features = extract_features(img)

        embeddings.append(features)
        filenames.append(file)

        print(f"✅ Processed: {file}")

    except Exception as e:
        print(f"❌ Skipped {file}: {e}")

# 🔥 Convert to numpy
embeddings = np.array(embeddings)

# 🔥 Ensure embeddings folder exists
os.makedirs(os.path.dirname(SAVE_PATH), exist_ok=True)

# 🔥 Save embeddings
np.save(SAVE_PATH, {
    "embeddings": embeddings,
    "filenames": filenames
})

print("\n🔥 Embeddings built successfully!")
print(f"Total images processed: {len(filenames)}")