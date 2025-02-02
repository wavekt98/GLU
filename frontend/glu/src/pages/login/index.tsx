/* eslint-disable jsx-a11y/no-noninteractive-tabindex */
/* eslint-disable jsx-a11y/no-static-element-interactions */
import InputItem from '@/components/common/inputs/inputItem';
import PrimaryButton from '@/components/common/buttons/primaryButton';
import { useState } from 'react';
import { LoginUser } from '@/types/UserTypes';
import { sweetalertError } from '@/utils/common';
import styles from '../userRegist.module.css';
import { loginAPI } from '../../utils/user/auth';

export default function LoginPage() {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async () => {
    const data: LoginUser = {
      id,
      password,
    };
    if (id === '' || password === '') {
      sweetalertError(
        '로그인 실패',
        '아이디와 비밀번호를 정확히 입력해주세요.',
      );
    } else {
      await loginAPI(data);
    }
  };

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter') {
      event.preventDefault();
      handleLogin();
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.section}>
        <div className={styles.title}>로그인</div>
        <div className={styles['input-container']}>
          <InputItem
            value={id}
            onChange={(e) => setId(e.target.value)}
            label="아이디"
            onKeyDown={handleKeyDown}
          />
          <InputItem
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            label="비밀번호"
            onKeyDown={handleKeyDown}
          />
        </div>
        <PrimaryButton label="로그인" size="medium" onClick={handleLogin} />
      </div>
    </div>
  );
}
