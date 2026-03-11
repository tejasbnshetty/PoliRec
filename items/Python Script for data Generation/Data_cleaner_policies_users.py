#!/usr/bin/env python3
"""
normalise_fleet.py
------------------
Clean / normalise vehicles in combined_dataset.json so that every
(make, model, type, fuel, transmission) combination matches the
authoritative list from Car_Brands_and_Models.pdf AND every
registered_on date is clamped to 05‑03‑2024 – 05‑05‑2025 inclusive,
stored in dd‑mm‑yyyy format.

Compatible with Python 3.7+
"""

import json
import pathlib
import random
import sys
from copy import deepcopy
from datetime import datetime, date
from typing import Optional

# --------------------------------------------------------------------
# Date limits and output format
# --------------------------------------------------------------------
MIN_DATE: date = date(2024, 3, 5)          # 05/03/2024 (d/m/y)
MAX_DATE: date = date(2025, 5, 5)          # 05/05/2025
DATE_OUTPUT_FMT = "%d-%m-%Y"               # ← dd‑mm‑yyyy as requested

def parse_any_date(s: str) -> Optional[date]:
    """Try a few common date formats, return None if all fail."""
    if not s:
        return None
    for fmt in ("%Y-%m-%d", "%d/%m/%Y", "%m/%d/%Y", "%d-%m-%Y"):
        try:
            return datetime.strptime(s, fmt).date()
        except ValueError:
            continue
    return None


# --------------------------------------------------------------------
# Canonical catalogue – EXACTLY five models per make
# --------------------------------------------------------------------

CATALOGUE = {
    "Toyota": [
        {"model": "Camry",   "type": "Sedan",     "fuel": "Petrol",  "trans": "Automatic"},
        {"model": "Corolla", "type": "Sedan",     "fuel": "Petrol",  "trans": "Manual"},
        {"model": "RAV4",    "type": "Suv",       "fuel": "Petrol",  "trans": "Automatic"},
        {"model": "Hilux",   "type": "Truck",     "fuel": "Diesel",  "trans": "Manual"},
        {"model": "Yaris",   "type": "Hatchback", "fuel": "Petrol",  "trans": "Manual"},
    ],
    "Ford": [
        {"model": "Ranger",        "type": "Truck",     "fuel": "Diesel",  "trans": "Automatic"},
        {"model": "Everest",       "type": "Suv",       "fuel": "Diesel",  "trans": "Automatic"},
        {"model": "Focus",         "type": "Hatchback", "fuel": "Petrol",  "trans": "Manual"},
        {"model": "Mustang Mach‑E","type": "Suv",       "fuel": "Electric","trans": "Automatic"},
        {"model": "Mondeo",        "type": "Sedan",     "fuel": "Petrol",  "trans": "Automatic"},
    ],
    "Tesla": [
        {"model": "Model 3",  "type": "Sedan", "fuel": "Electric", "trans": "Automatic"},
        {"model": "Model Y",  "type": "Suv",   "fuel": "Electric", "trans": "Automatic"},
        {"model": "Model X",  "type": "Suv",   "fuel": "Electric", "trans": "Automatic"},
        {"model": "Model S",  "type": "Sedan", "fuel": "Electric", "trans": "Automatic"},
        {"model": "CyberTruck","type": "Truck","fuel": "Electric", "trans": "Automatic"},
    ],
    "Hyundai": [
        {"model": "i20",            "type": "Hatchback", "fuel": "Petrol",  "trans": "Manual"},
        {"model": "Tucson",         "type": "Suv",       "fuel": "Diesel",  "trans": "Automatic"},
        {"model": "Kona Electric",  "type": "Suv",       "fuel": "Electric","trans": "Automatic"},
        {"model": "Elantra",        "type": "Sedan",     "fuel": "Petrol",  "trans": "Automatic"},
        {"model": "Santa Fe",       "type": "Suv",       "fuel": "Diesel",  "trans": "Manual"},
    ],
    "Honda": [
        {"model": "Civic", "type": "Sedan",     "fuel": "Petrol", "trans": "Manual"},
        {"model": "Jazz",  "type": "Hatchback", "fuel": "Petrol", "trans": "Automatic"},
        {"model": "CR‑V",  "type": "Suv",       "fuel": "Petrol", "trans": "Automatic"},
        {"model": "HR‑V",  "type": "Suv",       "fuel": "Petrol", "trans": "Automatic"},
        {"model": "City",  "type": "Sedan",     "fuel": "Petrol", "trans": "Manual"},
    ],
    "BMW": [
        {"model": "3 Series", "type": "Sedan",     "fuel": "Petrol",  "trans": "Automatic"},
        {"model": "X5",       "type": "Suv",       "fuel": "Diesel",  "trans": "Automatic"},
        {"model": "i4",       "type": "Sedan",     "fuel": "Electric","trans": "Automatic"},
        {"model": "1 Series", "type": "Hatchback", "fuel": "Petrol",  "trans": "Manual"},
        {"model": "X1",       "type": "Suv",       "fuel": "Petrol",  "trans": "Automatic"},
    ],
    "Mercedes‑Benz": [
        {"model": "C‑Class", "type": "Sedan", "fuel": "Diesel",  "trans": "Automatic"},
        {"model": "EQA",     "type": "Suv",   "fuel": "Electric","trans": "Automatic"},
        {"model": "GLA",     "type": "Suv",   "fuel": "Petrol",  "trans": "Automatic"},
        {"model": "A‑Class", "type": "Hatchback","fuel": "Petrol","trans": "Automatic"},
        {"model": "EQB",     "type": "Suv",   "fuel": "Electric","trans": "Automatic"},
    ],
    "Nissan": [
        {"model": "Navara",  "type": "Truck",     "fuel": "Diesel",  "trans": "Manual"},
        {"model": "Leaf",    "type": "Hatchback", "fuel": "Electric","trans": "Automatic"},
        {"model": "X‑Trail", "type": "Suv",       "fuel": "Petrol",  "trans": "Automatic"},
        {"model": "Altima",  "type": "Sedan",     "fuel": "Petrol",  "trans": "Manual"},
        {"model": "Qashqai", "type": "Suv",       "fuel": "Petrol",  "trans": "Automatic"},
    ],
    "Volkswagen": [
        {"model": "Golf",   "type": "Hatchback", "fuel": "Petrol",  "trans": "Manual"},
        {"model": "Passat", "type": "Sedan",     "fuel": "Diesel",  "trans": "Automatic"},
        {"model": "ID.4",   "type": "Suv",       "fuel": "Electric","trans": "Automatic"},
        {"model": "Tiguan", "type": "Suv",       "fuel": "Petrol",  "trans": "Automatic"},
        {"model": "Polo",   "type": "Hatchback", "fuel": "Petrol",  "trans": "Manual"},
    ],
    "Chevrolet": [
        {"model": "Silverado",   "type": "Truck",     "fuel": "Diesel",  "trans": "Automatic"},
        {"model": "Equinox",     "type": "Suv",       "fuel": "Petrol",  "trans": "Automatic"},
        {"model": "Bolt EV",     "type": "Hatchback", "fuel": "Electric","trans": "Automatic"},
        {"model": "Malibu",      "type": "Sedan",     "fuel": "Petrol",  "trans": "Automatic"},
        {"model": "Trailblazer", "type": "Suv",       "fuel": "Petrol",  "trans": "Manual"},
    ],
}

# Quick reverse lookup
MODEL_LOOKUP = {
    (make.lower(), spec["model"].lower()): spec
    for make, models in CATALOGUE.items()
    for spec in models
}
ALLOWED_MAKES = list(CATALOGUE.keys())

# --------------------------------------------------------------------
def choose_replacement():
    """Return a random (make, spec‑dict) pair from the catalogue."""
    make = random.choice(ALLOWED_MAKES)
    spec = random.choice(CATALOGUE[make])
    return make, deepcopy(spec)

# --------------------------------------------------------------------
def normalise_vehicle(v: dict) -> dict:
    """
    Return a normalised vehicle dict that
      • matches the catalogue (make/model/type/fuel/transmission)
      • has registered_on in dd‑mm‑yyyy format within the legal range.
    """
    # -------- make / model validation ------------------------------
    make_raw  = str(v.get("make",  "")).strip()
    model_raw = str(v.get("model", "")).strip()

    if not make_raw or not model_raw:
        make, spec = choose_replacement()
    else:
        model_key = model_raw.split()[0].lower()
        key = (make_raw.lower(), model_key)
        if key in MODEL_LOOKUP:
            make, spec = make_raw, MODEL_LOOKUP[key]
        else:
            make, spec = choose_replacement()

    cleaned = deepcopy(v)
    cleaned.update(
        make=make,
        model=spec["model"],
        type=spec["type"],
        fuel=spec["fuel"],
        transmission=spec["trans"],
    )

    # -------- registered_on clamping -------------------------------
    raw_reg = str(cleaned.get("registered_on", "")).strip()
    d = parse_any_date(raw_reg) or MIN_DATE
    if d < MIN_DATE:
        d = MIN_DATE
    elif d > MAX_DATE:
        d = MAX_DATE
    cleaned["registered_on"] = d.strftime(DATE_OUTPUT_FMT)

    return cleaned

# --------------------------------------------------------------------
def main(src_path: pathlib.Path, dst_path: pathlib.Path):
    random.seed(44)  # deterministic randomness

    # ---- load ------------------------------------------------------
    with src_path.open("r", encoding="utf‑8") as f:
        data = json.load(f)

    # ---- clean -----------------------------------------------------
    for user in data.get("users", {}).values():
        for rego, veh in list(user.get("vehicles", {}).items()):
            user["vehicles"][rego] = normalise_vehicle(veh)

    # ---- save ------------------------------------------------------
    with dst_path.open("w", encoding="utf‑8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)

    print(f"✔  Cleaned file written to {dst_path}")

# --------------------------------------------------------------------
if __name__ == "__main__":
    if len(sys.argv) < 2:
        sys.exit("Usage: python normalise_fleet.py <input_json> [output_json]")

    source = pathlib.Path(sys.argv[1])
    if not source.exists():
        sys.exit(f"Error: {source} not found")

    target = (
        pathlib.Path(sys.argv[2])
        if len(sys.argv) > 2
        else source.with_name(source.stem + "_clean.json")
    )
    main(source, target)
