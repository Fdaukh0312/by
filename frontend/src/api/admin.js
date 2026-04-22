import request from './request'

export const adminApi = {
  // ===== Dashboard =====
  getStats() {
    return request.get('/admin/dashboard/stats')
  },

  // ===== Product =====
  getProducts(params) {
    return request.get('/admin/product/list', { params })
  },
  getProduct(id) {
    return request.get(`/admin/product/detail/${id}`)
  },
  addProduct(data) {
    return request.post('/admin/product', data)
  },
  updateProduct(data) {
    return request.put('/admin/product', data)
  },
  setProductStatus(id, status) {
    return request.put(`/admin/product/status/${id}`, null, { params: { status } })
  },
  deleteProduct(id) {
    return request.delete(`/admin/product/${id}`)
  },

  // ===== Category =====
  getCategories() {
    return request.get('/admin/category/list')
  },
  getCategory(id) {
    return request.get(`/admin/category/${id}`)
  },
  addCategory(data) {
    return request.post('/admin/category', data)
  },
  updateCategory(data) {
    return request.put('/admin/category', data)
  },
  deleteCategory(id) {
    return request.delete(`/admin/category/${id}`)
  },

  // ===== Order =====
  getOrders(params) {
    return request.get('/admin/order/list', { params })
  },
  getOrder(id) {
    return request.get(`/admin/order/${id}`)
  },
  getOrderItems(id) {
    return request.get(`/admin/order/${id}/items`)
  },
  setOrderStatus(id, status) {
    return request.put(`/admin/order/${id}/status`, null, { params: { status } })
  }
}
