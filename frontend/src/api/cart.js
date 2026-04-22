import request from './request'

export const cartApi = {
  getList() {
    return request.get('/cart/list')
  },
  addToCart(data) {
    return request.post('/cart/add', data)
  },
  updateQuantity(id, quantity) {
    return request.put(`/cart/update/${id}?quantity=${quantity}`)
  },
  remove(id) {
    return request.delete(`/cart/${id}`)
  },
  clear() {
    return request.delete('/cart/clear')
  }
}
