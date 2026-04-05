

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
'''    results = []
    for file, score in scores[:top_k]:
        name = name_map.get(file, "Unknown")

        results.append({
            "file": file,
            "name": name,
            "confidence": round(score * 100, 2)
        })'''

    


# TEST
if __name__ == "__main__":
    input_path = "../uploads/input_sketch.png"

    results = recognize(input_path)

    print("\n🔍 TOP MATCHES:\n")
    for r in results:
        print(r)
     


''' 
import cv2
import numpy as np
import pandas as pd
from insightface.app import FaceAnalysis
from preprocess import preprocess_sketch
from utils import cosine_similarity

# Load model
app = FaceAnalysis(name="buffalo_l")
#app.prepare(ctx_id=0)
app.prepare(ctx_id=0, det_size=(640, 640))

# Load embeddings
data = np.load("../embeddings/photo_embeddings.npz", allow_pickle=True)
db_embeddings = data["embeddings"]
db_paths = data["paths"]

# Load sketch
sketch = preprocess_sketch("../uploads/input_sketch.png")

faces = app.get(sketch)

if len(faces) == 0:
    print("❌ No face detected in sketch")
    exit()

query_emb = faces[0].embedding

# Compare
best_score = -1
best_match = None

for emb, path in zip(db_embeddings, db_paths):
    score = cosine_similarity(query_emb, emb)

    if score > best_score:
        best_score = score
        best_match = path

print("\n✅ BEST MATCH:", best_match)
print("🔥 SIMILARITY SCORE:", best_score)



# Load names mapping
names_df = pd.read_csv("../database/mapping/names.csv", encoding="latin1")  
# assuming columns: image_id, name

# Convert to dict for fast lookup
name_map = dict(zip(names_df["filename"], names_df["name"]))

cv2.imshow("Sketch", sketch)
cv2.waitKey(0)

# ----------- MATCHING -----------
THRESHOLD = 0.3  # tune this

results = []
for file, score in scores[:top_k]:
    if score < THRESHOLD:
        continue

    name = name_map.get(file, "Unknown")

    results.append({
        "file": file,
        "name": name,
        "confidence": round(score * 100, 2)
    })

for emb, path in zip(db_embeddings, db_paths):
    score = cosine_similarity(query_emb, emb)

    # extract image filename (id)
    image_id = path.split("\\")[-1]  # Windows fix

    # get name from CSV
    person_name = name_map.get(image_id, "Unknown")

    results.append({
        "path": path,
        "image_id": image_id,
        "name": person_name,
        "score": score
    })

# Sort by similarity (descending)
results = sorted(results, key=lambda x: x["score"], reverse=True)

# ----------- NORMALIZE CONFIDENCE -----------

# convert cosine similarity → confidence %
scores = np.array([r["score"] for r in results])
min_s, max_s = scores.min(), scores.max()

for r in results:
    if max_s - min_s == 0:
        r["confidence"] = 100
    else:
        r["confidence"] = ((r["score"] - min_s) / (max_s - min_s)) * 100

# ----------- PRINT TOP 5 -----------

print("\n🎯 TOP 5 MATCHES:\n")

for i, r in enumerate(results[:5], 1):
    print(f"{i}. {r['name']}")
    print(f"   📁 Image: {r['image_id']}")
    print(f"   🔥 Similarity: {r['score']:.4f}")
    print(f"   ✅ Confidence: {r['confidence']:.2f}%\n")
    '''