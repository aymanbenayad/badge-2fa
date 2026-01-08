import os
from dotenv import load_dotenv
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives import serialization, hashes
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

load_dotenv()
PIN = os.getenv("PIN")

def to_java_array(name, data):
    bytes_str = ", ".join([f"(byte)0x{b:02X}" for b in data])
    return f"private static final byte[] {name} = {{ {bytes_str} }};"

def run():
    private_key = rsa.generate_private_key(public_exponent=65537, key_size=1024)
    public_key = private_key.public_key()
    
    pn = private_key.private_numbers()
    d = pn.d.to_bytes(128, byteorder='big')
    p = pn.p.to_bytes(64, byteorder='big')
    q = pn.q.to_bytes(64, byteorder='big')
    
    pub_n = public_key.public_numbers()
    modulus = pub_n.n.to_bytes(128, byteorder='big')
    exponent = pub_n.e.to_bytes(3, byteorder='big')

    pin_bytes = bytes([int(digit) for digit in PIN])
    
    digest = hashes.Hash(hashes.SHA256())
    digest.update(pin_bytes)
    aes_key = digest.finalize()[:16]
    
    def encrypt_comp(data, a_key):
        iv = os.urandom(16)
        cipher = Cipher(algorithms.AES(a_key), modes.CBC(iv))
        encryptor = cipher.encryptor()
        encrypted_data = encryptor.update(data) + encryptor.finalize()
        return iv + encrypted_data

    with open("rsa_vault_java.txt", "w") as f:
        f.write(to_java_array("RSA_MOD", modulus) + "\n")
        f.write(to_java_array("RSA_EXP", exponent) + "\n")
        f.write(to_java_array("ENC_D", encrypt_comp(d, aes_key)) + "\n")
        f.write(to_java_array("ENC_P", encrypt_comp(p, aes_key)) + "\n")
        f.write(to_java_array("ENC_Q", encrypt_comp(q, aes_key)) + "\n")

if __name__ == "__main__":
    run()