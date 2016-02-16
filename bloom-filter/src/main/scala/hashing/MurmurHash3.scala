package hashing

import java.lang.Long.rotateLeft

object MurmurHash3 {

  private val c1: Long = 0x87c37b91114253d5L
  private val c2: Long = 0x4cf5ad432745937fL

  def getLongLittleEndian(buf: Array[Byte], offset: Int): Long = {
    (buf(offset + 7).toLong << 56) |
        ((buf(offset + 6) & 0xffL) << 48) |
        ((buf(offset + 5) & 0xffL) << 40) |
        ((buf(offset + 4) & 0xffL) << 32) |
        ((buf(offset + 3) & 0xffL) << 24) |
        ((buf(offset + 2) & 0xffL) << 16) |
        ((buf(offset + 1) & 0xffL) << 8) |
        buf(offset) & 0xffL
  }

  def fmix64(l: Long): Long = {
    var k = l
    k ^= k >>> 33
    k *= 0xff51afd7ed558ccdL
    k ^= k >>> 33
    k *= 0xc4ceb9fe1a85ec53L
    k ^= k >>> 33
    k
  }

  def murmurhash3_x64_128(key: Array[Byte], offset: Int, len: Int, seed: Int): (Long, Long) = {
    var h1: Long = seed & 0x00000000FFFFFFFFL
    var h2: Long = seed & 0x00000000FFFFFFFFL

    val roundedEnd = offset + (len & 0xFFFFFFF0);  // round down to 16 byte block

    for (i <- offset.until(roundedEnd).by(16)) {
      var k1 = getLongLittleEndian(key, i)
      var k2 = getLongLittleEndian(key, i + 8)
      k1 *= c1; k1 = rotateLeft(k1, 31); k1 *= c2; h1 ^= k1
      h1 = rotateLeft(h1, 27); h1 += h2; h1 = h1 * 5 + 0x52dce729
      k2 *= c2; k2 = rotateLeft(k2, 33); k2 *= c1; h2 ^= k2
      h2 = rotateLeft(h2, 31); h2 += h1; h2 = h2 * 5 + 0x38495ab5
    }

    var k1: Long = 0
    var k2: Long = 0

    if (len == 15) k2 = (key(roundedEnd + 14) & 0xffL) << 48
    if (len >= 14) k2 |= (key(roundedEnd + 13) & 0xffL) << 40
    if (len >= 13) k2 |= (key(roundedEnd + 12) & 0xffL) << 32
    if (len >= 12) k2 |= (key(roundedEnd + 11) & 0xffL) << 24
    if (len >= 11) k2 |= (key(roundedEnd + 10) & 0xffL) << 16
    if (len >= 10) k2 |= (key(roundedEnd + 9) & 0xffL) << 8
    if (len >= 9) {
      k2 |= (key(roundedEnd + 8) & 0xffL)
      k2 *= c2
      k2 = rotateLeft(k2, 33)
      k2 *= c1
      h2 ^= k2
    }
    if (len >= 8) k1 = key(roundedEnd + 7).toLong << 56
    if (len >= 7) k1 |= (key(roundedEnd + 6) & 0xffL) << 48
    if (len >= 6) k1 |= (key(roundedEnd + 5) & 0xffL) << 40
    if (len >= 5) k1 |= (key(roundedEnd + 4) & 0xffL) << 32
    if (len >= 4) k1 |= (key(roundedEnd + 3) & 0xffL) << 24
    if (len >= 3) k1 |= (key(roundedEnd + 2) & 0xffL) << 16
    if (len >= 2) k1 |= (key(roundedEnd + 1) & 0xffL) << 8
    if (len >= 1) {
      k1 |= (key(roundedEnd) & 0xffL)
      k1 *= c1
      k1 = rotateLeft(k1, 31)
      k1 *= c2
      h1 ^= k1
    }

    h1 ^= len; h2 ^= len

    h1 += h2
    h2 += h1

    h1 = fmix64(h1)
    h2 = fmix64(h2)

    h1 += h2
    h2 += h1

    (h1, h2)
  }
}
