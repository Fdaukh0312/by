import { defineStore } from 'pinia'
import { ref } from 'vue'
import { cartApi } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const cartCount = ref(0)

  const updateCartCount = async () => {
    try {
      const res = await cartApi.getList()
      cartCount.value = res.data?.length || 0
    } catch (error) {
      console.error('获取购物车数量失败:', error)
    }
  }

  return {
    cartCount,
    updateCartCount
  }
})
