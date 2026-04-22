import request from './request'

export const orderApi = {
  create(data) {
    return request.post('/order/create', data)
  },
  getList(params) {
    return request.get('/order/list', { params })
  },
  getOrderItems(id) {
    return request.get(`/order/${id}/items`)
  },
  cancel(id) {
    return request.put(`/order/${id}/cancel`)
  }
}
