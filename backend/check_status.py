import requests

# 管理员token
token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc1NTY1NzQ4NywiZXhwIjoxNzU1NzQzODg3fQ.2cIjycEjdbQG8wQRp8s43OFOIqSE5hNoHiNAmkNNdMY'

# 获取所有评论
response = requests.get('http://localhost:8080/api/admin/comments', 
                       headers={'Authorization': f'Bearer {token}'}, 
                       params={'page': 1, 'size': 20})

print('Response status:', response.status_code)
data = response.json()
print('Total comments:', data['data']['total'])

# 查找评论5
records = data['data']['records']
comment5 = None
for record in records:
    if record['id'] == 5:
        comment5 = record
        break

if comment5:
    print('\n=== Comment 5 Details ===')
    print('ID:', comment5['id'])
    print('Deleted:', comment5['deleted'])
    print('Status:', comment5['status'])
    print('Content:', comment5['content'][:100] + '...' if len(comment5['content']) > 100 else comment5['content'])
    print('Create Time:', comment5['createTime'])
    print('Update Time:', comment5['updateTime'])
else:
    print('Comment 5 not found')

# 检查前几条评论的状态
print('\n=== First 5 Comments Status ===')
for i, record in enumerate(records[:5]):
    print(f"ID: {record['id']}, Deleted: {record['deleted']}, Status: {record['status']}")