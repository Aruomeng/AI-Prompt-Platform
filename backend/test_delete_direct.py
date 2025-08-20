import requests
import json
import time

token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1NTY1NzQ4NywiZXhwIjoxNzU1NzQzODg3fQ.2cIjycEjdbQG8wQRp8s43OFOIqSE5hNoHiNAmkNNdMY'

# 1. 首先获取评论5的详细信息
print("=== 获取评论5的详细信息 ===")
response = requests.get('http://localhost:8080/api/admin/comments', 
                       headers={'Authorization': f'Bearer {token}'}, 
                       params={'page': 1, 'size': 20})
data = response.json()['data']
comment_5 = next(c for c in data['records'] if c['id'] == 5)
print(f"评论5当前状态: ID={comment_5['id']}, Deleted={comment_5['deleted']}, UserID={comment_5['user']['id']}")

# 2. 使用普通用户接口删除评论5
print("\n=== 使用普通用户接口删除评论5 ===")
delete_response = requests.delete('http://localhost:8080/api/comments/5', 
                                headers={'Authorization': f'Bearer {token}'})
print("Delete status:", delete_response.status_code)
print("Delete response:", delete_response.json())

# 3. 等待1秒让操作完成
time.sleep(1)

# 4. 再次检查评论状态
print("\n=== 删除后检查评论状态 ===")
response = requests.get('http://localhost:8080/api/admin/comments', 
                       headers={'Authorization': f'Bearer {token}'}, 
                       params={'page': 1, 'size': 20})
data = response.json()['data']
for c in data['records']:
    if c['id'] == 5:
        print(f"评论5最终状态: ID={c['id']}, Deleted={c['deleted']}")

# 5. 检查普通用户接口获取的评论
print("\n=== 普通用户接口获取的评论 ===")
response = requests.get('http://localhost:8080/api/comments/prompt/5', 
                       headers={'Authorization': f'Bearer {token}'})
comments = response.json()['data']
print(f"提示词5的评论数量: {len(comments)}")
for c in comments:
    print(f"ID: {c['id']}, Deleted: {c['deleted']}, Content: {c['content'][:20]}")