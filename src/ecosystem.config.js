const botsPath = '<YOUR-PATH-HERE>';
const python = `python3`;

const telegramBot1Script = `${botsPath}/main.py`;
const telegramBotRepliesScript = `${botsPath}/replies.py`;

const bot1 = {
  name: 'hesa-ondc-telegram-bot1',
  script: telegramBot1Script,
  cwd: botsPath,
  watch: false,
  exec_mode: 'fork_mode',
  interpreter: python,
  error_file: `${botsPath}/logs/telegram-bot1.log`,
  out_file: `${botsPath}/logs/telegram-bot1.log`,
  merge_logs: true,
  autorestart: true,
  restart_delay: 3000,
  instances: 1,
  min_uptime: '2m',
  max_restarts: 1000,
};

module.exports = {
  apps: [
    bot1,
  ]
};
