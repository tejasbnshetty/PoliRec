import firebase_admin
from firebase_admin import credentials, db

cred = credentials.Certificate("/content/carregoapp-firebase-adminsdk-fbsvc-9d0be0c037.json")

firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://carregoapp-default-rtdb.firebaseio.com/'
})

# Reference to users and policies
users_ref = db.reference('users')
policies_ref = db.reference('policies')
notifications_ref = db.reference('user_notifications')

# Fetching all license numbers (keys in 'users' node)
users_snapshot = users_ref.get()
license_keys = list(users_snapshot.keys())
print(f"Fetched {len(license_keys)} license keys.")

# Fetching all policy numbers
policies_snapshot = policies_ref.get()
policy_numbers = [policy['policy_no'] for policy in policies_snapshot.values() if 'policy_no' in policy]
print(f"Fetched {len(policy_numbers)} policies.")

for license_key in license_keys:
    updates = {f"policy_{policy_no}": True for policy_no in policy_numbers}
    notifications_ref.child(license_key).set(updates)
    print(f"Updated notifications for: {license_key}")

print("user_notifications setup completed.")