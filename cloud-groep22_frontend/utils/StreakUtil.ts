const STREAK_KEY = 'user_streak';
const LAST_LOGIN_KEY = 'last_login';

export const updateStreak = async (): Promise<number> => {
    const today = new Date().toISOString().split('T')[0];
    const lastLogin = localStorage.getItem(LAST_LOGIN_KEY);

    let streak = parseInt(localStorage.getItem(STREAK_KEY) || '0', 10);

    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const yesterdayStr = yesterday.toISOString().split('T')[0];

    if (lastLogin === today) {
        return streak;
    }

    if (lastLogin === yesterdayStr) {
        streak += 1;
    } else {
        streak = 1;
    }

    localStorage.setItem(STREAK_KEY, streak.toString());
    localStorage.setItem(LAST_LOGIN_KEY, today);

    return streak;
};