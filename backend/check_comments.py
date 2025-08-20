import requests
import json

token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1NTY1NzQ4NywiZXhwIjoxNzU1NzQzODg3fQ.2cIjycEjdbQG8wQRp8s43OFOIqSE5hNoHiNAmkNNdMY'

# 获取提示词5的评论
response = requests.get('http://localhost:8080/api/comments/prompt/5', headers={'Authorization': f'Bearer {token}'})
print("=== 普通用户接口获取评论 ===")
print("Status:", response.status_code)
comments = response.json()['data']
print("评论数量:", len(comments))
for c in comments:
    print(f"ID: {c['id']}, Content: {c['content'][:30]}..., Deleted: {c['deleted']}")

# 获取管理员接口的所有评论
response = requests.get('http://localhost:8080/api/admin/comments', headers={'Authorization': f'Bearer {token}'}, params={'page': 1, 'size': 20})
print("\n=== 管理员接口获取所有评论 ===")
print("Status:", response.status_code)
data = response.json()['data']
print("总评论数:", data['total'])
for c in data['records']:
    print(f"ID: {c['id']}, Prompt: {c['promptId']}, Deleted: {c['deleted']}")