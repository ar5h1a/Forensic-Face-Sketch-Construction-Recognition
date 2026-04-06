

import numpy as np
import csv
from utils import preprocess_image, extract_features, cosine_similarity

# Load embeddings
data = np.load("../embeddings/photo_embeddings.npy", allow_pickle=True).item()
db_embeddings = data["embeddings"]
db_files = data["filenames"]

# Load names
name_map = {}
with open("../database/mapping/names.csv", mode="r") as file:
    reader = csv.DictReader(file)
    for row in reader:
        #name_map[row["filename"]] = row["name"]
        key = row["filename"].split(".")[0]
        name_map[key] = row["name"]


def recognize(input_image_path, top_k=5):
    img = preprocess_image(input_image_path)
    query_features = extract_features(img)

    scores = []

    for emb, file in zip(db_embeddings, db_files):
        sim = cosine_similarity(query_features, emb)
        scores.append((file, sim))

    # Sort by similarity
    scores.sort(key=lambda x: x[1], reverse=True)

    THRESHOLD = 0.3  # tune this

    results = []
    for file, score in scores[:top_k]:
        if score < THRESHOLD:
            continue

        file_key = file.split(".")[0]   # remove extension
        name = name_map.get(file_key, "Unknown")
        #name = name_map.get(file, "Unknown")

        results.append({
            "file": file,
            "name": name,
            "confidence": round(score * 100, 2)
        })

    return results

    


# TEST
if __name__ == "__main__":
    input_path = "../uploads/input_sketch.png"

    results = recognize(input_path)

    print("\n🔍 TOP MATCHES:\n")
    for r in results:
        print(r)
     


