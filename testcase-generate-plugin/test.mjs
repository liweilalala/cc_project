import path from 'path';
import fs from 'fs';
import os from 'os';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const normalizePath = (p, homeDir) => {
  if (!p || typeof p !== 'string') return null;
  let normalized = p.trim();
  if (!normalized) return null;
  if (normalized.startsWith('~/')) {
    normalized = path.join(homeDir, normalized.slice(2));
  } else if (normalized === '~') {
    normalized = homeDir;
  }
  return path.resolve(normalized);
};

const getSkillsDirs = () => {
  const skillsBase = path.resolve(__dirname, 'skills');
  const agents = ['test-req-preprocessor', 'test-requirement-analyst', 'test-design-expert'];
  return agents
    .map(agent => path.join(skillsBase, agent, 'SKILL.md'))
    .filter(file => fs.existsSync(file));
};

const homeDir = os.homedir();
const skillsDirs = getSkillsDirs();

console.log('Home dir:', homeDir);
console.log('Skills base:', path.resolve(__dirname, '../skills'));
console.log('Found skills:');
skillsDirs.forEach(s => console.log(' -', s));

const passed = skillsDirs.length === 3;
console.log('\nTest', passed ? 'PASSED' : 'FAILED');
process.exit(passed ? 0 : 1);