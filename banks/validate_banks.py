import os
import json
import re
import sys

# Ensure UTF-8 output on Windows console
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")

def normalize_sms(text: str) -> str:
    # 1. Convert Persian/Arabic digits to Latin
    persian_arabic = {
        '۰': '0', '۱': '1', '۲': '2', '۳': '3', '۴': '4',
        '۵': '5', '۶': '6', '۷': '7', '۸': '8', '۹': '9',
        '٠': '0', '١': '1', '٢': '2', '٣': '3', '٤': '4',
        '٥': '5', '٦': '6', '٧': '7', '٨': '8', '٩': '9'
    }
    for k, v in persian_arabic.items():
        text = text.replace(k, v)
        
    # 2. Convert Arabic characters to Persian
    text = text.replace('ي', 'ی').replace('ك', 'ک')
    
    # 3. Remove thousand separators
    text = re.sub(r'(?<=\d)[,،٬\u066C](?=\d)', '', text)
    
    # 4. Collapse multiple whitespaces
    text = re.sub(r'[\t ]+', ' ', text)
    
    return text.strip()

def to_python_regex(java_regex: str) -> str:
    # Java/Kotlin uses (?<name>\d+), Python uses (?P<name>\d+)
    return re.sub(r'\(\?<([a-zA-Z0-9_]+)>', r'(?P<\1>', java_regex)

def validate_all_banks(directory: str):
    print("========================================")
    print("Validating Iranian Bank Pattern Files...")
    print("========================================")
    
    files = [f for f in os.listdir(directory) if f.endswith(".json") and f != "schema.json"]
    if not files:
        print("No bank json files found!")
        sys.exit(1)
        
    errors = 0
    passed = 0
    
    for filename in sorted(files):
        path = os.path.join(directory, filename)
        with open(path, "r", encoding="utf-8") as f:
            try:
                data = json.load(f)
            except Exception as e:
                print(f"[FAIL] {filename}: Invalid JSON format: {e}")
                errors += 1
                continue
                
        bank_id = data.get("id")
        name_fa = data.get("name_fa")
        deposit_regex_str = data.get("deposit_regex")
        
        if not bank_id or not name_fa or not deposit_regex_str:
            print(f"[FAIL] {filename}: Missing required fields (id, name_fa, or deposit_regex)")
            errors += 1
            continue
            
        py_regex = to_python_regex(deposit_regex_str)
        try:
            dep_pattern = re.compile(py_regex, re.MULTILINE)
        except Exception as e:
            print(f"[FAIL] {filename}: Invalid deposit_regex syntax: {e}")
            errors += 1
            continue
            
        samples = data.get("sample_sms", [])
        sample_passed = True
        for sample in samples:
            raw = sample.get("raw", "")
            expected_amount = sample.get("expected_amount_rial")
            normalized = normalize_sms(raw)
            
            if sample.get("type") == "DEPOSIT":
                match = dep_pattern.search(normalized)
                if not match:
                    print(f"[FAIL] {filename}: Deposit regex did NOT match sample: {raw}")
                    sample_passed = False
                    errors += 1
                    break
                parsed_amount = int(match.group("amount"))
                if expected_amount and parsed_amount != expected_amount:
                    print(f"[FAIL] {filename}: Amount mismatch! Expected {expected_amount}, got {parsed_amount}")
                    sample_passed = False
                    errors += 1
                    break
                    
        if sample_passed:
            print(f"[OK] {filename} ({name_fa} / {bank_id}) - Verified with {len(samples)} samples")
            passed += 1
            
    print("========================================")
    print(f"Summary: {passed} passed, {errors} errors.")
    print("========================================")
    
    if errors > 0:
        sys.exit(1)

if __name__ == "__main__":
    banks_dir = os.path.dirname(os.path.abspath(__file__))
    validate_all_banks(banks_dir)
