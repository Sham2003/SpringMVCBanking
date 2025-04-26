"""
predict.py – Flask micro‑service for loan‑approval prediction
------------------------------------------------------------

> POST  /predict
> Body: JSON with the 9 numeric feature fields.
> Returns: {"approved": "approved"}  or  {"approved": "rejected"}

Training snippet (one‑off):
    import pandas as pd, joblib
    from sklearn.ensemble import GradientBoostingClassifier
    df = pd.read_csv("loans.csv")
    FEATURES = [...]  # same order as FEATURE_ORDER below
    X = df[FEATURES]
    y = df["approved"].map({"Y": 1, "N": 0})
    model = GradientBoostingClassifier().fit(X, y)
    joblib.dump(model, "gb_loans.pkl")
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import joblib
import numpy as np
import os

app = Flask(__name__)
CORS(app)

# --- 1. Load trained model --------------------------------------------------
MODEL_PATH = os.getenv("MODEL_PATH", "loan_model.pkl")

try:
    model = joblib.load(MODEL_PATH)
except FileNotFoundError:
    raise RuntimeError(f"Model file '{MODEL_PATH}' not found! "
                       "Train a model first or set MODEL_PATH env var.")

# --- 2. Ensure incoming JSON uses this order --------------------------------
FEATURE_ORDER = [
    "no_of_dependents",
    "income_annum",
    "loan_amount",
    "loan_term",
    "cibil_score",
    "residential_assets_value",
    "commercial_assets_value",
    "luxury_assets_value",
    "bank_asset_value",
]

# --- 3. Prediction endpoint --------------------------------------------------
@app.post("/predict")
def predict():
    data = request.get_json(force=True)

    # Basic validation: check all features exist
    missing = [f for f in FEATURE_ORDER if f not in data]
    if missing:
        print(missing)
        return jsonify(error=f"Missing fields: {', '.join(missing)}"), 400

    try:
        # Convert to np.float64 row vector
        row = np.array([[float(data[f]) for f in FEATURE_ORDER]], dtype=np.float64)

        # Predict: 1 = approved, 0 = rejected (adjust if your model differs)
        pred = model.predict(row)[0]
        print(row)
        print(pred)

        label = "approved" if pred == 1 else "rejected"
        return jsonify(approved=label)

    except Exception as ex:
        print(ex)
        return jsonify(error=str(ex)), 500


if __name__ == "__main__":
    PORT = int(os.getenv("PORT", 5000))
    app.run(host="0.0.0.0", port=PORT)
