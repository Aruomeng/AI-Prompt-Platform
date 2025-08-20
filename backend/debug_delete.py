import requests
import json

token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1NTY1NzQ4NywiZXhwIjoxNzU1NzQzODg3fQ.2cIjycEjdbQG8wQRp8s43OFOIqSE5hNoHiNAmkNNdMY'

# 获取管理员接口的所有评论
print("=== 管理员接口获取所有评论 ===")
response = requests.get('http://localhost:8080/api/admin/comments', 
                       headers={'Authorization': f'Bearer {token}'}, 
                       params={'page': 1, 'size': 20})
print("Status:", response.status_code)
data = response.json()['data']
print("总评论数:", data['total'])
for c in data['records']:
    print(f"ID: {c['id']}, Prompt: {c['promptId']}, Deleted: {c['deleted']}, Content: {c['content'][:30]}")

# 尝试删除评论5
print("\n=== 尝试删除评论5 ===")
delete_response = requests.delete('http://localhost:8080/api/admin/comments/5', 
                                headers={'Authorization': f'Bearer {token}'})
print("Delete status:", delete_response.status_code)
print("Delete response:", delete_response.json())

# 再次检查评论状态
print("\n=== 删除后检查评论状态 ===")
response = requests.get('http://localhost:8080/api/admin/comments', 
                       headers={'Authorization': f'Bearer {token}'}, 
                       params={'page': 1, 'size': 20})
data = response.json()['data']
for c in data['records']:
    if c['id'] == 5:
        print(f"评论5状态: ID={c['id']}, Deleted={c['deleted']}")