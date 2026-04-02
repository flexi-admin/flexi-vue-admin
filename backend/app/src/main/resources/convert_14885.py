import json
import re
import time

# 读取14885.js文件
with open('14885.js', 'r', encoding='utf-8') as f:
    content = f.read()

# 提取zNodes数组
match = re.search(r'var zNodes = (\[.*?\]);', content, re.DOTALL)
if not match:
    print('未找到zNodes数组')
    exit(1)

# 解析JSON数据
z_nodes_str = match.group(1)
z_nodes = json.loads(z_nodes_str)

# 生成SQL插入语句
sql_statements = []
sql_statements.append('-- 资产类型数据')
sql_statements.append('INSERT INTO asset_type (id, name, code, parent_id, path, level, remark, status, create_time, update_time) VALUES')

# 生成当前时间戳
timestamp = int(time.time() * 1000)

# 用于存储id映射，因为js中的code和我们的id可能不同
id_counter = 1
id_map = {}

# 递归处理节点
def process_node(node, parent_id=0, parent_path='', level=1):
    global id_counter
    
    # 生成当前节点的id
    current_id = id_counter
    id_counter += 1
    
    # 存储code到id的映射
    id_map[node['code']] = current_id
    
    # 生成path
    if parent_path:
        path = f"{parent_path},{current_id}"
    else:
        path = str(current_id)
    
    # 提取code和name
    code_match = re.search(r'[\(（](.*?)[\)）]', node['name'])
    code = code_match.group(1) if code_match else ''
    name = re.sub(r'\s*[\(（].*?[\)）]', '', node['name'])
    
    # 生成SQL语句
    sql = f"({current_id}, '{name}', '{code}', {parent_id}, '{path}', {level}, '{node['name']}', 1, {timestamp}, {timestamp})"
    sql_statements.append(sql)
    
    # 处理子节点
    if 'children' in node:
        for child in node['children']:
            process_node(child, current_id, path, level + 1)

# 处理所有根节点
for node in z_nodes:
    process_node(node)

# 合并SQL语句
for i in range(1, len(sql_statements) - 1):
    sql_statements[i] = sql_statements[i] + ','

# 写入SQL文件
with open('14885.sql', 'w', encoding='utf-8') as f:
    f.write('\n'.join(sql_statements))

print('转换完成，生成了14885.sql文件')