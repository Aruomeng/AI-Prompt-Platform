#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
密码加密测试脚本
用于测试和验证密码加密结果

使用方式：
python password-encrypt-test.py [密码]
或
python password-encrypt-test.py --interactive
"""

import hashlib
import sys
import getpass


def encrypt_password(password):
    """
    MD5加密函数（与后端一致）
    :param password: 原始密码
    :return: 加密后的密码
    """
    return hashlib.md5(password.encode('utf-8')).hexdigest()


def test_common_passwords():
    """批量测试常见密码"""
    common_passwords = [
        'admin123',
        '123456',
        'password',
        'test123',
        'user123',
        'abc123',
        'qwerty',
        '111111',
        'admin',
        'root',
        'guest',
        '123456789',
        'iloveyou',
        'monkey',
        'dragon'
    ]

    print("=== 常见密码测试结果 ===")
    for pwd in common_passwords:
        encrypted = encrypt_password(pwd)
        print(f"{pwd.ljust(12)} -> {encrypted}")


def verify_known_passwords():
    """验证已知密码"""
    known_passwords = [
        {'original': 'admin123', 'encrypted': '0192023a7bbd73250516acf069cf1903'},
        {'original': 'test123', 'encrypted': 'cc03e747a6afbbcbf8be7668acfebee5'},
        {'original': 'password', 'encrypted': '5f4dcc3b5aa765d61d8327deb882cf99'}
    ]

    print("\n=== 已知密码验证 ===")
    for item in known_passwords:
        encrypted = encrypt_password(item['original'])
        match = "✓" if encrypted == item['encrypted'] else "✗"
        print(f"{item['original'].ljust(12)} -> {encrypted} {match}")


def interactive_mode():
    """交互模式"""
    print("=== 密码加密测试工具 ===")
    print("输入密码进行加密测试，输入exit退出\n")

    while True:
        try:
            password = getpass.getpass("请输入密码: ")
            if password.lower() == 'exit':
                print("感谢使用！")
                break

            if password.strip():
                encrypted = encrypt_password(password.strip())
                print(f"加密结果: {encrypted}")
                print(f"长度: {len(encrypted)} 字符\n")
            else:
                print("密码不能为空！\n")
        except KeyboardInterrupt:
            print("\n感谢使用！")
            break


def main():
    """主函数"""
    if len(sys.argv) == 1:
        # 无参数时显示帮助
        print("=== 密码加密测试脚本 ===")
        print("用法:")
        print("  python password-encrypt-test.py [密码]     - 加密单个密码")
        print("  python password-encrypt-test.py --common   - 测试常见密码")
        print("  python password-encrypt-test.py --verify   - 验证已知密码")
        print("  python password-encrypt-test.py --interactive - 交互模式")
        return

    first_arg = sys.argv[1]

    if first_arg == '--common':
        test_common_passwords()
        verify_known_passwords()
    elif first_arg == '--verify':
        verify_known_passwords()
    elif first_arg == '--interactive':
        interactive_mode()
    else:
        # 单个密码加密
        password = ' '.join(sys.argv[1:])
        encrypted = encrypt_password(password)
        print(f"原始密码: {password}")
        print(f"加密结果: {encrypted}")
        print(f"长度: {len(encrypted)} 字符")


if __name__ == "__main__":
    main()