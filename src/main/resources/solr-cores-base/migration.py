import re
import json
import pysolr
import uuid
import copy
from datetime import datetime

# import http.client
# http.client.HTTPConnection.debuglevel = 1

# Připojení k nové Solr instanci (verze 9)
new_solr_meta = pysolr.Solr('http://localhost:8983/solr/metatitle', always_commit=True)
new_solr_volume = pysolr.Solr('http://localhost:8983/solr/volume', always_commit=True)
new_solr_exemplar = pysolr.Solr('http://localhost:8983/solr/specimen', always_commit=True)
new_solr_users = pysolr.Solr('http://localhost:8983/solr/user', always_commit=True)
new_solr_owners = pysolr.Solr('http://localhost:8983/solr/owner', always_commit=True)
new_solr_mutations = pysolr.Solr('http://localhost:8983/solr/mutation', always_commit=True)
new_solr_editions = pysolr.Solr('http://localhost:8983/solr/edition', always_commit=True)

# Připojení ke staré Solr instanci (verze 8)
old_solr_meta = pysolr.Solr('http://localhost:8984/solr/titul', always_commit=True)
old_solr_volume = pysolr.Solr('http://localhost:8984/solr/svazek', always_commit=True)
old_solr_exemplar = pysolr.Solr('http://localhost:8984/solr/exemplar', always_commit=True)
old_solr_users = pysolr.Solr('http://localhost:8984/solr/user', always_commit=True)


now = datetime.now()
current_time = now.strftime('%Y-%m-%dT%H:%M:%S.%f') + '{:03d}'.format(now.microsecond % 1000) + 'Z'

def is_valid_date_format(date_string):
    try:
        # Pokusí se převést datum na objekt datetime
        datetime.strptime(date_string, "%Y-%m-%d")
        return True
    except ValueError:
        return False

def extract_damaged_pages(json_string):
    if not json_string:
        return []  # Return empty list if input is empty string or None

    try:
        data = json.loads(json_string)
        damaged = data.get("damaged", [])
        return [int(num) for num in damaged]  # Convert each element to integer
    except (json.JSONDecodeError, AttributeError):
        return []  # Handle JSON decoding errors or attribute errors gracefully


def extract_missing_pages(json_string):
    if not json_string:
        return []  # Return empty list if input is empty string or None

    try:
        data = json.loads(json_string)
        damaged = data.get("missing", [])
        return [int(num) for num in damaged]  # Convert each element to integer
    except (json.JSONDecodeError, AttributeError):
        return []  # Handle JSON decoding errors or attribute errors gracefully


def roman_to_int(roman):
    roman_numerals = {
        'I': 1, 'V': 5, 'X': 10, 'L': 50,
        'C': 100, 'D': 500, 'M': 1000
    }

    total = 0
    prev_value = 0

    for char in reversed(roman):
        value = roman_numerals.get(char, 0)
        if value < prev_value:
            total -= value
        else:
            total += value
        prev_value = value

    return total


def convert_year(year):
    if year is None:
        return 0

    # Check if the year is already an integer
    if isinstance(year, int):
        return year

    # Check if the year is a string
    elif isinstance(year, str):
        year = year.strip()

        # Check if it is a simple numeric year
        if year.isdigit():
            return int(year)

        # Check if it is a Roman numeral
        try:
            return roman_to_int(year)
        except ValueError:
            pass

        # Check if it is in the format YYYY-MM-DD
        match = re.match(r'^(\d{4})-\d{2}-\d{2}$', year)
        if match:
            return int(match.group(1))

        # Handle any other formats or invalid input
        return 0

    # Handle other types of input
    return 0


def transform_periodicity(old_data, editions_mapping):
    # Load the old data if it's in string format
    if isinstance(old_data, str):
        old_data = json.loads(old_data)

    # Mapping of old field names to new field names
    field_mapping = {
        "den": "day",
        "active": "numExists",
        "vydani": "editionId",
        "pocet_stran": "pagesCount",
        "nazev": "name",
        "podnazev": "subName",
        "isPriloha": "isAttachment"
    }

    # Transform the data
    new_data = []
    for entry in old_data:
        transformed_entry = {}
        for old_field, new_field in field_mapping.items():
            if new_field:  # Skip fields with an empty new_field name
                if new_field == "editionId":
                    valid_values = {'0', '1', '2', '3', '4', '5', '6'}
                    transformed_entry[new_field] = editions_mapping.get(entry.get(old_field) if entry.get(old_field) in valid_values else '0')
                elif new_field == "pagesCount":
                    transformed_entry[new_field] = extract_number(entry.get(old_field, "0"))
                elif new_field == "isAttachment":
                    transformed_entry[new_field] = bool(entry.get(old_field, False))
                else:
                    transformed_entry[new_field] = entry.get(old_field, "")
        new_data.append(transformed_entry)

    return json.dumps(new_data, ensure_ascii=False)


def extract_number(value):
    if value is None:
        value = "0"
    else:
        value = str(value)
    # Odstranění všech nečíselných znaků kromě "-" (který odděluje rozsahy)
    cleaned_value = re.sub(r'[^0-9\-]', '', value)

    # Rozdělení podle "-" a výběr prvního čísla
    if '-' in cleaned_value:
        cleaned_value = cleaned_value.split('-')[0]

    # Převod na celé číslo (int)
    try:
        number = int(cleaned_value)
    except ValueError:
        number = 0  # Pokud se nepodaří převést na int, vrátí se 0

    return number


def generate_uuid():
    return str(uuid.uuid4())


def create_initial_data():
    # Edice
    editions = [
        {"id": generate_uuid(), "name": {"cs": "Bez označení", "sk": "Bez označenia", "en": "Without marking"}, "isDefault": True, "isAttachment": False, "isPeriodicAttachment": False, "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": {"cs": "Ranní", "sk": "Ranné", "en": "Morning"}, "isDefault": False, "isAttachment": False, "isPeriodicAttachment": False, "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": {"cs": "Polední", "sk": "Poludnie", "en": "Midday"}, "isDefault": False, "isAttachment": False, "isPeriodicAttachment": False, "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": {"cs": "Odpolední", "sk": "Popoludnie", "en": "Afternoon"}, "isDefault": False, "isAttachment": False, "isPeriodicAttachment": False, "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": {"cs": "Večerní", "sk": "Večerné", "en": "Evening"}, "isDefault": False, "isAttachment": False, "isPeriodicAttachment": False, "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": {"cs": "Jiné", "sk": "Iné", "en": "Other"}, "isDefault": False, "isAttachment": False, "isPeriodicAttachment": False, "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": {"cs": "Jiná příloha", "sk": "Iná príloha", "en": "Another attachment"}, "isDefault": False, "isAttachment": True, "isPeriodicAttachment": False, "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": {"cs": "Pravidelná příloha", "sk": "Pravidelná príloha", "en": "Periodic attachment"}, "isDefault": False, "isAttachment": True, "isPeriodicAttachment": True, "created": current_time, "createdBy": "migration-script"}
    ]
    # Create a new list for Solr submissions
    solr_editions = copy.deepcopy(editions)

    # Convert the 'name' field to a JSON string in the new list
    for edition in solr_editions:
        edition['name'] = json.dumps(edition['name'])

    # Add editions to Solr
    new_solr_editions.add(solr_editions)

    volume_mappings = ["", "morning", "midday", "afternoon", "evening", "special", "attachment", "periodic_attachment"]
    for pub, volume_mapping in zip(editions, volume_mappings):
        pub["volumeMapping"] = volume_mapping

    # Mutace
    mutations = [
        {"id": generate_uuid(), "name": {"cs": "Bez označení", "sk": "Bez označenia", "en": "Without marking"}, "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": {"cs": "Brno", "sk": "Brno", "en": "Brno"}, "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": {"cs": "Praha", "sk": "Praha", "en": "Praha"}, "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": {"cs": "Ostrava", "sk": "Ostrava", "en": "Ostrava"}, "created": current_time, "createdBy": "migration-script"},
    ]
    solr_mutations = copy.deepcopy(mutations)

    # Convert the 'name' field to a JSON string in the new list
    for mutation in solr_mutations:
        mutation['name'] = json.dumps(mutation['name'])

    # Add mutations to Solr
    new_solr_mutations.add(solr_mutations)

    # Vlastníci
    owners = [
        {"id": generate_uuid(), "name": "Národní knihovna České republiky", "shorthand": "NKP", "sigla": "ABA001", "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": "Moravská zemská knihovna v Brně", "shorthand": "MZK", "sigla": "BOA001", "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": "Vědecká knihovna v Olomouci", "shorthand": "VKOL", "sigla": "OLA001", "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": "Knihovna Ústeckého kraje, příspěvková organizace", "shorthand": "KUK", "sigla": "ULG001", "created": current_time, "createdBy": "migration-script"},
        {"id": generate_uuid(), "name": "Krajská knihovna v Pardubicích", "shorthand": "PaKK", "sigla": "PAG001", "created": current_time, "createdBy": "migration-script"}
    ]
    new_solr_owners.add(owners)

    return {
        "editions": {str(idx): pub["id"] for idx, pub in enumerate(editions)},
        "editionsForVolume": {pub["volumeMapping"]: pub["id"] for pub in editions},
        "mutations": {str(idx): mut["id"] for idx, mut in enumerate(mutations)},
        "owners": {str(idx): owner["id"] for idx, owner in enumerate(owners)}
    }


def migrate_user(owners_mapping):
    old_users = old_solr_users.search('*:*', rows=1000000)  # Adjust the number of rows as needed
    new_users = []

    for user in old_users:
        nazev = user.get("nazev")
        new_user = {
            "id": generate_uuid(),
            "userName": user.get("username").strip(),
            "email": user.get("email", "").strip(),
            "role": user.get("role").strip(),
            "active": user.get("active", False),
            "owners": owners_mapping.get(user.get("owner".strip())),
        }

        if nazev:
            name_parts = nazev.split()

            new_user["firstName"] = name_parts[0].strip() if len(name_parts) > 0 else ""
            new_user["lastName"] = name_parts[1].strip() if len(name_parts) > 1 else ""
        else:
            new_user["firstName"] = ""
            new_user["lastName"] = ""
        if new_user.get("email") and len(new_user.get("userName")) > 0 and new_user.get("userName") != "admin":
            new_users.append(new_user)

    new_solr_users.add(new_users)


def migrate_metatitle():
    old_meta_titles = old_solr_meta.search('*:*', rows=1000000)  # Adjust the number of rows as needed
    new_meta_titles = []
    old_meta_titles_ids = []

    for meta in old_meta_titles:
        new_meta = {
            "id": generate_uuid(),
            "name": meta.get("meta_nazev", "").strip(),
            # "periodicity": meta.get("periodicita").strip(),
            "note": meta.get("poznamka", "").strip(),
            "isPublic": meta.get("show_to_not_logged_users", True),
            "created": current_time,
            "createdBy": "migration-script"
        }
        old_meta_titles_ids.append(meta.get("id"))
        new_meta_titles.append(new_meta)

    new_solr_meta.add(new_meta_titles)

    for pub, meta_title_mapping in zip(new_meta_titles, old_meta_titles_ids):
        pub["old_id"] = meta_title_mapping
    return {meta["old_id"]: meta["id"] for meta in new_meta_titles}  # Return mapping for further use


def migrate_volume(meta_title_mapping, mutations_mapping, owners_mapping, editions_mapping):
    old_volumes = old_solr_volume.search('*:*', rows=1000000)  # Adjust the number of rows as needed
    new_volumes = []


    for vol in old_volumes:
        periodicity = transform_periodicity(vol.get("periodicita").strip(), editions_mapping)
        new_vol = {
            "id": generate_uuid(),
            "barCode": vol.get("carovy_kod").strip(),
            "dateFrom": vol.get("datum_od").strip(),
            "dateTo": vol.get("datum_do").strip(),
            "metaTitleId": meta_title_mapping.get(vol.get("id_titul").strip(), ""),
            "mutationId": mutations_mapping.get(vol.get("mutace").strip()),
            "subName": "",
            "periodicity": periodicity,
            "firstNumber": vol.get("prvni_cislo").strip(),
            "lastNumber": vol.get("posledni_cislo").strip(),
            "note": vol.get("poznamka", "").strip(),
            "showAttachmentsAtTheEnd": vol.get("show_attachments_at_the_end", False),
            "signature": vol.get("signatura", "").strip(),
            "ownerId": owners_mapping.get(vol.get("vlastnik").strip()),
            "year": convert_year(vol.get("year")) if vol.get("year") and len(vol.get("year")) > 0 else convert_year(vol.get("datum_od", "").strip()),
            "mutationMark": vol.get("znak_oznaceni_vydani", "").strip(),
            "created": current_time,
            "createdBy": "migration-script"
        }

        if new_vol["metaTitleId"] is not None and len(new_vol["metaTitleId"]) == 36 and new_vol["barCode"] is not None and len(new_vol["barCode"]) > 0:
            new_volumes.append(new_vol)

    new_solr_volume.add(new_volumes)
    return {vol["barCode"]: (vol["id"], vol["periodicity"], vol["dateFrom"], vol["dateTo"]) for vol in new_volumes}  # Return mapping for further use


def migrate_exemplar(meta_title_mapping, volume_mapping, owners_mapping, editions_mapping, mutations_mapping):
    old_exemplars = old_solr_exemplar.search('*:*', rows=1000000)  # Adjust the number of rows as needed
    new_exemplars = []
    exemplars_with_num_missing = {}
    exemplars_with_bad_date = {}

    for ex in old_exemplars:
        # Extract the publication date from the exemplar
        publication_date_orig = re.sub(r"T.*", "", ex.get("datum_vydani").strip()) if ex.get("datum_vydani") else None
        publication_datetime = datetime.strptime(publication_date_orig,"%Y-%m-%d") if publication_date_orig else None
        publication_date_string = datetime.strptime(publication_date_orig, "%Y-%m-%d").strftime("%Y%m%d") if publication_date_orig else None

        # Get the volume data from the volume mapping using the carovy_kod
        bar_code = ex.get("carovy_kod", "").strip()
        volume_id, periodicity, volume_date_from, volume_date_to = volume_mapping.get(bar_code, ("", [], "", ""))


        new_ex = {
            "id": generate_uuid(),
            "metaTitleId": meta_title_mapping.get(ex.get("id_titul").strip(), ""),
            # "metaTitleName": ex.get("meta_nazev").strip(),
            "volumeId": volume_id,
            "barCode": ex.get("carovy_kod", "").strip(),
            "numExists": ex.get("numExists", False),
            "numMissing": ex.get("missing_number", False),
            # "signature": ex.get("signatura").strip(),
            "ownerId": owners_mapping.get(ex.get("vlastnik").strip(), ""),
            "damageTypes": ex.get("stav", []),
            "damagedPages": extract_damaged_pages(ex.get("pages").strip()),
            "missingPages": extract_missing_pages(ex.get("pages").strip()),
            "note": ex.get("poznamka", "").strip(),
            "name": ex.get("nazev", "").strip(),
            "subName": ex.get("podnazev", "").strip(),
            "editionId": editions_mapping.get(ex.get("vydani").strip(), editions_mapping.get("0")),
            "mutationId": mutations_mapping.get(ex.get("mutace").strip()),
            "mutationMark": ex.get("znak_oznaceni_vydani", "").strip(),
            "publicationDate": publication_date_orig if publication_date_orig else None,
            "publicationDateString": publication_date_string if publication_date_orig else None,
            "number": ex.get("cislo", None) if not ex.get("isPriloha", False) else None,
            "attachmentNumber": ex.get("cislo", None) if ex.get("isPriloha", False) else None,
            "pagesCount": ex.get("pocet_stran"),
            "isAttachment": ex.get("isPriloha", False),
            "created": current_time,
            "createdBy": "migration-script"
        }




        # Now check if this exemplar is missing based on periodicity and numExists flag
        if publication_datetime and volume_date_from and volume_date_to and not new_ex["numMissing"] and is_valid_date_format(new_ex["publicationDate"]):
            volume_start_date = datetime.strptime(volume_date_from, "%Y-%m-%d")
            volume_end_date = datetime.strptime(volume_date_to, "%Y-%m-%d")

            if volume_start_date <= publication_datetime <= volume_end_date:
                # Check if the exemplar's publication date corresponds to a day in the periodicity
                day_of_week = publication_datetime.strftime("%A")  # Get the day of the week (e.g., "Monday")
                transformed_periodicity = json.loads(periodicity)
                # print(transformed_periodicity)
                for period in transformed_periodicity:
                    # print(period["day"])
                    if period["day"] == day_of_week and period["numExists"] and not new_ex["numExists"]:
                        # If numExists is false, we need to set numMissing to true for this exemplar
                        new_ex["numMissing"] = True
                        new_ex["number"] = None
                        new_ex["attachmentNumber"] = None
                        break

        # Store exemplars with numMissing = true in the dictionary under their volume_id
        if new_ex["numMissing"]:
            if volume_id not in exemplars_with_num_missing:
                exemplars_with_num_missing[volume_id] = []
            exemplars_with_num_missing[volume_id].append(new_ex)

        if new_ex["publicationDate"] is not None and len(new_ex["publicationDate"]) >= 10 and new_ex["volumeId"] is not None and len(new_ex["volumeId"]) == 36 and new_ex["metaTitleId"] is not None and len(new_ex["metaTitleId"]) == 36:
            new_exemplars.append(new_ex)



    new_solr_exemplar.add(new_exemplars)

    # Now print out the volume_id and its corresponding exemplars with numMissing
    for volume_id, exemplars in exemplars_with_num_missing.items():
        print(f"Volume ID: {volume_id}")
        for exemplar in exemplars:
            print(f"{exemplar['publicationDate']}")
    # print(len(exemplars_with_num_missing.items()))


def main():
    mappings = create_initial_data()
    # migrate_user(mappings["owners"])
    meta_title_mapping = migrate_metatitle()
    volume_mapping = migrate_volume(meta_title_mapping, mappings["mutations"], mappings["owners"], mappings["editions"])
    migrate_exemplar(meta_title_mapping, volume_mapping, mappings["owners"], mappings["editions"],
                     mappings["mutations"])


if __name__ == "__main__":
    main()
