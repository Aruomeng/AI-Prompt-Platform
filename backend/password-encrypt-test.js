#!/usr/bin/env node

/**
 * 密码加密测试脚本
 * 用于测试和验证密码加密结果
 * 
 * 使用方式：
 * node password-encrypt-test.js [密码]
 * 或
 * node password-encrypt-test.js --interactive
 */

const crypto = require('crypto');

/**
 * MD5加密函数（与后端一致）
 * @param {string} password - 原始密码
 * @returns {string} - 加密后的密码
 */
function encryptPassword(password) {
    return crypto.createHash('md5').update(password).digest('hex');
}

/**
 * 批量测试常见密码
 */
function testCommonPasswords() {
    const commonPasswords = [
        'admin123',
        '123456',
        'password',
        'test123',
        'user123',
        'abc123',
        'qwerty',
        '111111',
        'admin',
        'root'
    ];

    console.log('=== 常见密码测试结果 ===');
    commonPasswords.forEach(pwd => {
        const encrypted = encryptPassword(pwd);
        console.log(`${pwd.padEnd(12)} -> ${encrypted}`);
    });
}

/**
 * 验证已知密码
 */
function verifyKnownPasswords() {
    const knownPasswords = [
        { original: 'admin123', encrypted: '0192023a7bbd73250516acf069cf1903' },
        { original: 'test123', encrypted: 'cc03e747a6afbbcbf8be7668acfebee5' }
    ];

    console.log('\n=== 已知密码验证 ===');
    knownPasswords.forEach(item => {
        const encrypted = encryptPassword(item.original);
        const match = encrypted === item.encrypted;
        console.log(`${item.original.padEnd(12)} -> ${encrypted} ${match ? '✓' : '✗'}`);
    });
}

/**
 * 交互模式
 */
function interactiveMode() {
    const readline = require('readline');
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });

    console.log('=== 密码加密测试工具 ===');
    console.log('输入密码进行加密测试，输入exit退出\n');

    const askPassword = () => {
        rl.question('请输入密码: ', (password) => {
            if (password.toLowerCase() === 'exit') {
                console.log('感谢使用！');
                rl.close();
                return;
            }

            if (password.trim()) {
                const encrypted = encryptPassword(password.trim());
                console.log(`加密结果: ${encrypted}`);
                console.log(`长度: ${encrypted.length} 字符\n`);
            } else {
                console.log('密码不能为空！\n');
            }

            askPassword();
        });
    };

    askPassword();
}

/**
 * 主函数
 */
function main() {
    const args = process.argv.slice(2);

    if (args.length === 0) {
        // 无参数时显示帮助
        console.log('=== 密码加密测试脚本 ===');
        console.log('用法:');
        console.log('  node password-encrypt-test.js [密码]     - 加密单个密码');
        console.log('  node password-encrypt-test.js --common   - 测试常见密码');
        console.log('  node password-encrypt-test.js --verify   - 验证已知密码');
        console.log('  node password-encrypt-test.js --interactive - 交互模式');
        return;
    }

    const firstArg = args[0];

    switch (firstArg) {
        case '--common':
            testCommonPasswords();
            verifyKnownPasswords();
            break;
        case '--verify':
            verifyKnownPasswords();
            break;
        case '--interactive':
            interactiveMode();
            break;
        default:
            // 单个密码加密
            const password = args.join(' ');
            const encrypted = encryptPassword(password);
            console.log(`原始密码: ${password}`);
            console.log(`加密结果: ${encrypted}`);
            console.log(`长度: ${encrypted.length} 字符`);
    }
}

// 运行主函数
if (require.main === module) {
    main();
}

// 导出函数供其他脚本使用
module.exports = { encryptPassword };