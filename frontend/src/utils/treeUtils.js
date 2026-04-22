// 树形结构通用工具函数

/**
 * 计算所有可选的父节点选项
 * @param {Array} treeData - 树形数据
 * @param {string|number} currentEditingId - 当前编辑的节点ID
 * @returns {Array} 父节点选项数组
 */
export const getParentOptions = (treeData, currentEditingId) => {
  const options = []
  
  // 根节点
  options.push({ label: '根节点', value: 0 })
  
  // 检查节点是否是当前编辑节点的子孙后代
  const isDescendant = (nodeId, targetId) => {
    if (!targetId || !nodeId || !treeData) return false
    
    const findNode = (nodes, id) => {
      if (!nodes || !Array.isArray(nodes)) return null
      
      for (const node of nodes) {
        if (!node) continue
        if (node.id === id) {
          return node
        }
        if (node.children && node.children.length > 0) {
          const result = findNode(node.children, id)
          if (result) {
            return result
          }
        }
      }
      return null
    }
    
    const targetNode = findNode(treeData, targetId)
    if (!targetNode) return false
    
    const checkDescendant = (node) => {
      if (!node) return false
      if (node.id === nodeId) {
        return true
      }
      if (node.children && node.children.length > 0) {
        for (const child of node.children) {
          if (checkDescendant(child)) {
            return true
          }
        }
      }
      return false
    }
    
    return checkDescendant(targetNode)
  }
  
  // 递归遍历所有节点
  const traverse = (nodes, path = '') => {
    if (!nodes || !Array.isArray(nodes)) return
    
    for (const node of nodes) {
      if (!node || !node.id) continue
      
      // 排除当前编辑的节点及其子孙后代
      if (node.id !== currentEditingId && !isDescendant(node.id, currentEditingId)) {
        const nodePath = path ? path + '-' + node.name : node.name
        options.push({ label: nodePath, value: node.id })
      }
      
      // 递归遍历子节点
      if (node.children && node.children.length > 0) {
        const childPath = path ? path + '-' + node.name : node.name
        traverse(node.children, childPath)
      }
    }
  }
  
  traverse(treeData)
  return options
}