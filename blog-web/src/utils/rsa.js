import JSEncrypt from 'jsencrypt'
import request from './request'

let publicKeyCache = null

export async function getPublicKey() {
    if (publicKeyCache) return publicKeyCache
    const res = await request.get('/admin/public-key')
    publicKeyCache = res.data.publicKey
    return publicKeyCache
}

export async function encryptPassword(password) {
    const publicKey = await getPublicKey()
    const encrypt = new JSEncrypt()
    encrypt.setPublicKey(publicKey)
    return encrypt.encrypt(password)
}
