

# import numpy as np
# import csv
# from scripts.utils import preprocess_image, extract_features, cosine_similarity

# # Load embeddings
# data = np.load("../embeddings/photo_embeddings.npy", allow_pickle=True).item()
# db_embeddings = data["embeddings"]
# db_files = data["filenames"]

# # Load names
# name_map = {}
# with open("../database/mapping/names.csv", mode="r") as file:
#     reader = csv.DictReader(file)
#     for row in reader:
#         #name_map[row["filename"]] = row["name"]
#         key = row["filename"].split(".")[0]
#         name_map[key] = row["name"]


# def recognize(input_image_path, top_k=5):
#     img = preprocess_image(input_image_path)
#     query_features = extract_features(img)

#     scores = []

#     for emb, file in zip(db_embeddings, db_files):
#         sim = cosine_similarity(query_features, emb)
#         scores.append((file, sim))

#     # Sort by similarity
#     scores.sort(key=lambda x: x[1], reverse=True)

#     THRESHOLD = 0.3  # tune this

#     results = []
#     for file, score in scores[:top_k]:
#         if score < THRESHOLD:
#             continue

#         file_key = file.split(".")[0]   # remove extension
#         name = name_map.get(file_key, "Unknown")
#         #name = name_map.get(file, "Unknown")

#         results.append({
#             "file": file,
#             "name": name,
#             "confidence": round(score * 100, 2)
#         })

#     return results

    


# # TEST
# if __name__ == "__main__":
#     input_path = "../uploads/input_sketch.png"

#     results = recognize(input_path)

#     print("\n🔍 TOP MATCHES:\n")
#     for r in results:
#         print(r)
     


import os
import numpy as np
import csv
from scripts.utils import preprocess_image, extract_features, cosine_similarity

# 🔥 Base directory (forensic-backend root)
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# 🔥 Paths
EMBEDDINGS_PATH = os.path.join(BASE_DIR, "embeddings", "photo_embeddings.npy")
NAMES_PATH = os.path.join(BASE_DIR, "database", "mapping", "names.csv")

# 🔥 Load embeddings
data = np.load(EMBEDDINGS_PATH, allow_pickle=True).item()
db_embeddings = data["embeddings"]
db_files = data["filenames"]

# 🔥 Load name mapping
name_map = {}
with open(NAMES_PATH, mode="r") as file:
    reader = csv.DictReader(file)
    for row in reader:
        key = row["filename"].split(".")[0]
        name_map[key] = row["name"]


def recognize(input_image_path, top_k=5):
    # 🔥 Preprocess input image
    img = preprocess_image(input_image_path)
    query_features = extract_features(img)

    scores = []

    # 🔥 Compare with database
    for emb, file in zip(db_embeddings, db_files):
        sim = cosine_similarity(query_features, emb)
        scores.append((file, sim))

    # 🔥 Sort by similarity (descending)
    scores.sort(key=lambda x: x[1], reverse=True)

    THRESHOLD = 0.3  # you can tune this

    results = []
    for file, score in scores[:top_k]:
        if score < THRESHOLD:
            continue

        file_key = file.split(".")[0]
        name = name_map.get(file_key, "Unknown")

        results.append({
            "file": file,
            "name": name,
            "confidence": round(score * 100, 2)
        })

    return results


# 🔥 Standalone testing
if __name__ == "__main__":
    test_path = os.path.join(BASE_DIR, "uploads", "input_sketch.png")

    results = recognize(test_path)

    print("\n🔍 TOP MATCHES:\n")
    for r in results:
        print(r)