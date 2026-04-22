import request from './request'

export const categoryApi = {
  getList() {
    return request.get('/category/list')
  }
}
