import requests

# 管理员token
token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1NTY1NzQ4NywiZXhwIjoxNzU1NzQzODg3fQ.2cIjycEjdbQG8wQRp8s43OFOIqSE5hNoHiNAmkNNdMY'

print("=== 删除前状态 ===")
response = requests.get('http://localhost:8080/api/admin/comments', 
                       headers={'Authorization': f'Bearer {token}'}, 
                       params={'page': 1, 'size': 20})

data = response.json()['data']['records']
comment5 = None
for record in data:
    if record['id'] == 5:
        comment5 = record
        break

if comment5:
    print(f"删除前 - ID: {comment5['id']}, Deleted: {comment5['deleted']}, Status: {comment5['status']}")
else:
    print("评论5未找到")

print("\n=== 执行删除操作 ===")
delete_response = requests.delete('http://localhost:8080/api/admin/comments/5', 
                               headers={'Authorization': f'Bearer {token}'})
print(f"删除响应状态: {delete_response.status_code}")
print(f"删除响应内容: {delete_response.json()}")

print("\n=== 删除后状态 ===")
response = requests.get('http://localhost:8080/api/admin/comments', 
                       headers={'Authorization': f'Bearer {token}'}, 
                       params={'page': 1, 'size': 20})

data = response.json()['data']['records']
comment5 = None
for record in data:
    if record['id'] == 5:
        comment5 = record
        break

if comment5:
    print(f"删除后 - ID: {comment5['id']}, Deleted: {comment5['deleted']}, Status: {comment5['status']}")
else:
    print("评论5未找到")