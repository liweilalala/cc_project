/**
 * TestDesigner plugin for OpenCode.ai
 *
 * Auto-registers all agent skills directories via config hook.
 */

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
  const skillsBase = path.resolve(__dirname, '../../skills');
  const agents = ['test-req-preprocessor', 'test-requirement-analyst', 'test-design-expert'];
  return agents
    .map(agent => path.join(skillsBase, agent, 'testcase-generate-workflow'))
    .filter(dir => fs.existsSync(dir));
};

export const TestDesignerPlugin = async ({ client, directory }) => {
  const homeDir = os.homedir();
  const skillsDirs = getSkillsDirs();

  return {
    config: async (config) => {
      config.skills = config.skills || {};
      config.skills.paths = config.skills.paths || [];
      for (const dir of skillsDirs) {
        if (!config.skills.paths.includes(dir)) {
          config.skills.paths.push(dir);
        }
      }
    },

    'experimental.chat.messages.transform': async (_input, output) => {
      if (!output.messages.length) return;
      const firstUser = output.messages.find(m => m.info.role === 'user');
      if (!firstUser || !firstUser.parts.length) return;

      if (firstUser.parts.some(p => p.type === 'text' && p.text.includes('TESTCASE_GENERATE_WORKFLOW'))) return;

      const bootstrap = `**TESTCASE_GENERATE_WORKFLOW**
Available agents for test case generation:
- test-req-preprocessor: Requirement analysis workflow
- test-requirement-analyst: Test requirement analysis workflow
- test-design-expert: Test design workflow

Use the \`skill\` tool to load specific workflow skills.`;

      const ref = firstUser.parts[0];
      firstUser.parts.unshift({ ...ref, type: 'text', text: bootstrap });
    }
  };
};