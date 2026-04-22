import request from './request'

export const productApi = {
  getList(params) {
    return request.get('/product/list', { params })
  },
  getDetail(id) {
    return request.get(`/product/detail/${id}`)
  },
  getRecommend(limit = 10) {
    return request.get('/product/recommend', { params: { limit } })
  }
}
