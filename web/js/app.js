/**
 * One Piece Battle Simulation System - Main Web Application Logic
 */

// Application State
const state = {
  characters: [],
  crews: [],
  history: [],
  selectedFighter1: null,
  selectedFighter2: null,
  isBattleInProgress: false,
  playbackSpeed: 600, // ms per round
  battleAbortController: false
};

// Avatar & Icon Mapper
function getCharacterAvatar(char) {
  const name = char.name.toLowerCase();
  if (name.includes('luffy')) return '👒';
  if (name.includes('zoro')) return '⚔️';
  if (name.includes('nami')) return '🧭';
  if (name.includes('law')) return '💉';
  if (name.includes('shanks')) return '🗡️';
  if (name.includes('katakuri')) return '🍩';
  if (name.includes('crocodile')) return '🐊';
  if (name.includes('sanji')) return '🔥';
  if (name.includes('chopper')) return '🦌';
  if (name.includes('robin')) return '📖';
  if (name.includes('franky')) return '🤖';
  if (name.includes('brook')) return '💀';
  if (name.includes('jinbe')) return '🥋';

  switch (char.role?.toLowerCase()) {
    case 'captain': return '👑';
    case 'swordsman': return '🗡️';
    case 'navigator': return '🗺️';
    case 'devilfruituser': return '🍇';
    default: return '🏴‍☠️';
  }
}

function getRoleClass(role) {
  switch (role?.toLowerCase()) {
    case 'captain': return 'role-captain';
    case 'swordsman': return 'role-swordsman';
    case 'navigator': return 'role-navigator';
    case 'devilfruituser': return 'role-devilfruituser';
    default: return 'role-captain';
  }
}

// UI Initialization
document.addEventListener('DOMContentLoaded', () => {
  initNavTabs();
  initSoundToggle();
  initModals();
  loadAllData();
  checkTomcatStatus();

  // Search and filter listeners
  const searchInput = document.getElementById('charSearchInput');
  if (searchInput) {
    searchInput.addEventListener('input', (e) => filterCharacters(e.target.value));
  }

  const roleFilterBtns = document.querySelectorAll('.filter-btn');
  roleFilterBtns.forEach(btn => {
    btn.addEventListener('click', (e) => {
      roleFilterBtns.forEach(b => b.classList.remove('active'));
      e.target.classList.add('active');
      const role = e.target.getAttribute('data-role');
      filterCharactersByRole(role);
    });
  });

  // Fighter Select dropdown listeners
  document.getElementById('fighter1Select')?.addEventListener('change', (e) => {
    selectFighter(1, e.target.value);
  });
  document.getElementById('fighter2Select')?.addEventListener('change', (e) => {
    selectFighter(2, e.target.value);
  });

  // Randomize Matchup button
  document.getElementById('randomMatchBtn')?.addEventListener('click', randomizeMatchup);

  // Commence Battle button
  document.getElementById('commenceBattleBtn')?.addEventListener('click', startBattleSimulation);

  // Speed controls
  document.querySelectorAll('.theater-speed-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      document.querySelectorAll('.theater-speed-btn').forEach(b => b.classList.remove('active'));
      e.target.classList.add('active');
      state.playbackSpeed = parseInt(e.target.getAttribute('data-speed'));
    });
  });

  // Rematch button
  document.getElementById('rematchBtn')?.addEventListener('click', () => {
    document.getElementById('victoryCard')?.classList.remove('active');
    startBattleSimulation();
  });

  // Clear history button
  document.getElementById('clearHistoryBtn')?.addEventListener('click', clearBattleHistory);

  // Run Demo button in Polymorphism lab
  document.getElementById('runDemoBtn')?.addEventListener('click', runPolymorphismDemo);
});

/* ==========================================================================
   NAVIGATION
   ========================================================================== */
function initNavTabs() {
  const tabs = document.querySelectorAll('.nav-tab');
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      window.soundEngine?.playClick();
      const targetPanelId = tab.getAttribute('data-tab');

      tabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');

      document.querySelectorAll('.tab-panel').forEach(panel => {
        panel.classList.remove('active');
      });

      const targetPanel = document.getElementById(targetPanelId);
      if (targetPanel) {
        targetPanel.classList.add('active');
      }

      if (targetPanelId === 'labTab') {
        runPolymorphismDemo();
      }
    });
  });
}

function initSoundToggle() {
  const soundBtn = document.getElementById('soundToggleBtn');
  if (!soundBtn) return;

  const updateSoundBtnText = () => {
    const isMuted = window.soundEngine?.isMuted();
    soundBtn.innerHTML = isMuted ? '🔇 Sound: Off' : '🔊 Sound: On';
  };

  updateSoundBtnText();
  soundBtn.addEventListener('click', () => {
    window.soundEngine?.toggleMute();
    updateSoundBtnText();
    window.soundEngine?.playClick();
  });
}

/* ==========================================================================
   API CALLS & DATA LOADING
   ========================================================================== */
async function loadAllData() {
  await Promise.all([
    fetchCharacters(),
    fetchCrews(),
    fetchHistory()
  ]);
  populateFighterSelects();
}

async function checkTomcatStatus() {
  try {
    const res = await fetch('api/status');
    if (res.ok) {
      const data = await res.json();
      const statusPill = document.getElementById('tomcatStatusPill');
      if (statusPill) {
        statusPill.innerHTML = `<span class="status-dot"></span> Tomcat Active (${data.servletVersion || '10.1'})`;
      }
    }
  } catch (e) {
    console.warn('Could not query status endpoint:', e);
  }
}

async function fetchCharacters() {
  try {
    const res = await fetch('api/characters');
    if (!res.ok) throw new Error('Failed to load characters');
    state.characters = await res.json();
    renderCharactersGrid(state.characters);
    updateTabBadge('charsBadge', state.characters.length);
  } catch (err) {
    showToast('Failed to load characters: ' + err.message, 'error');
  }
}

async function fetchCrews() {
  try {
    const res = await fetch('api/crews');
    if (!res.ok) throw new Error('Failed to load crews');
    state.crews = await res.json();
    renderCrewsGrid(state.crews);
    updateTabBadge('crewsBadge', state.crews.length);
    populateCrewSelectOptions();
  } catch (err) {
    showToast('Failed to load crews: ' + err.message, 'error');
  }
}

async function fetchHistory() {
  try {
    const res = await fetch('api/history');
    if (!res.ok) throw new Error('Failed to load history');
    const data = await res.json();
    state.history = data;
    renderHistoryList(data);
    updateTabBadge('historyBadge', data.totalMatches || 0);
  } catch (err) {
    console.warn('Could not fetch history:', err);
  }
}

function updateTabBadge(badgeId, count) {
  const badge = document.getElementById(badgeId);
  if (badge) badge.textContent = count;
}

/* ==========================================================================
   CHARACTER ROSTER RENDERING & FILTERING
   ========================================================================== */
function renderCharactersGrid(chars) {
  const grid = document.getElementById('characterRosterGrid');
  if (!grid) return;

  if (!chars || chars.length === 0) {
    grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: var(--text-dim); padding: 3rem;">No pirate characters found.</div>`;
    return;
  }

  grid.innerHTML = chars.map(char => {
    const avatar = getCharacterAvatar(char);
    const roleCls = getRoleClass(char.role);
    const abilities = (char.abilities || []).map(a => `<span class="ability-pill">${a}</span>`).join('');

    const hpPercent = Math.min(100, Math.round((char.maxHealth / 150) * 100));
    const atkPercent = Math.min(100, Math.round((char.attackPower / 100) * 100));
    const defPercent = Math.min(100, Math.round((char.defense / 40) * 100));

    return `
      <div class="character-card" data-role="${char.role}">
        <div class="char-card-header">
          <div>
            <div class="char-card-name">${avatar} ${escapeHtml(char.name)}</div>
            <div class="char-card-trait">${escapeHtml(char.specialTrait || '')}</div>
          </div>
          <span class="char-card-role-badge ${roleCls}">${char.role}</span>
        </div>

        <div class="stat-bars-container">
          <div class="stat-row">
            <span class="stat-name">HP</span>
            <div class="stat-progress-bg">
              <div class="stat-progress-bar stat-bar-hp" style="width: ${hpPercent}%"></div>
            </div>
            <span class="stat-value-text">${char.maxHealth}</span>
          </div>
          <div class="stat-row">
            <span class="stat-name">ATK</span>
            <div class="stat-progress-bg">
              <div class="stat-progress-bar stat-bar-atk" style="width: ${atkPercent}%"></div>
            </div>
            <span class="stat-value-text">${char.attackPower}</span>
          </div>
          <div class="stat-row">
            <span class="stat-name">DEF</span>
            <div class="stat-progress-bg">
              <div class="stat-progress-bar stat-bar-def" style="width: ${defPercent}%"></div>
            </div>
            <span class="stat-value-text">${char.defense}</span>
          </div>
        </div>

        <div class="abilities-tag-cloud">
          ${abilities}
        </div>

        <div class="char-card-actions">
          <button class="btn-card-action" onclick="prepareFighterForArena('${escapeHtml(char.name)}')">Select for Arena</button>
          <button class="btn-card-action danger" onclick="deleteCharacter('${escapeHtml(char.name)}')">Dismiss</button>
        </div>
      </div>
    `;
  }).join('');
}

function filterCharacters(searchTerm) {
  const term = searchTerm.toLowerCase().trim();
  const filtered = state.characters.filter(c => 
    c.name.toLowerCase().includes(term) ||
    c.role.toLowerCase().includes(term) ||
    (c.specialTrait && c.specialTrait.toLowerCase().includes(term))
  );
  renderCharactersGrid(filtered);
}

function filterCharactersByRole(role) {
  if (!role || role === 'all') {
    renderCharactersGrid(state.characters);
  } else {
    const filtered = state.characters.filter(c => c.role.toLowerCase() === role.toLowerCase());
    renderCharactersGrid(filtered);
  }
}

async function deleteCharacter(name) {
  if (!confirm(`Are you sure you want to dismiss ${name} from the registry?`)) return;

  try {
    const res = await fetch(`api/characters?name=${encodeURIComponent(name)}`, {
      method: 'DELETE'
    });
    const data = await res.json();
    if (res.ok) {
      showToast(`Character ${name} dismissed.`, 'success');
      window.soundEngine?.playClick();
      await loadAllData();
    } else {
      showToast(data.message || 'Error dismissing character', 'error');
    }
  } catch (err) {
    showToast('Failed to delete character: ' + err.message, 'error');
  }
}

/* ==========================================================================
   BATTLE ARENA SETUP & EXECUTION
   ========================================================================== */
function populateFighterSelects() {
  const select1 = document.getElementById('fighter1Select');
  const select2 = document.getElementById('fighter2Select');
  if (!select1 || !select2) return;

  const optionsHtml = '<option value="">-- Choose Combatant --</option>' +
    state.characters.map(c => `<option value="${escapeHtml(c.name)}">${getCharacterAvatar(c)} ${escapeHtml(c.name)} (${c.role})</option>`).join('');

  select1.innerHTML = optionsHtml;
  select2.innerHTML = optionsHtml;

  // Set default selection if none
  if (state.characters.length >= 2) {
    select1.value = state.characters[0].name;
    select2.value = state.characters[1].name;
    selectFighter(1, state.characters[0].name);
    selectFighter(2, state.characters[1].name);
  }
}

function selectFighter(slot, charName) {
  const char = state.characters.find(c => c.name === charName);
  if (slot === 1) state.selectedFighter1 = char;
  if (slot === 2) state.selectedFighter2 = char;

  updateFighterPreview(slot, char);
  validateBattleReady();
}

function updateFighterPreview(slot, char) {
  const card = document.getElementById(`fighter${slot}Card`);
  if (!card) return;

  const avatarEl = document.getElementById(`fighter${slot}Avatar`);
  const nameEl = document.getElementById(`fighter${slot}NameDisplay`);
  const subtextEl = document.getElementById(`fighter${slot}Subtext`);
  const hpTextEl = document.getElementById(`fighter${slot}HpText`);
  const hpFillEl = document.getElementById(`fighter${slot}HpFill`);
  const atkEl = document.getElementById(`fighter${slot}AtkVal`);
  const defEl = document.getElementById(`fighter${slot}DefVal`);
  const roleEl = document.getElementById(`fighter${slot}RoleBadge`);

  if (!char) {
    avatarEl.textContent = '❓';
    nameEl.textContent = 'Choose Fighter';
    subtextEl.textContent = 'Select a character above';
    hpTextEl.textContent = '0 / 0';
    hpFillEl.style.width = '100%';
    atkEl.textContent = '-';
    defEl.textContent = '-';
    roleEl.textContent = 'Unselected';
    roleEl.className = 'fighter-role-badge';
    return;
  }

  avatarEl.textContent = getCharacterAvatar(char);
  nameEl.textContent = char.name;
  subtextEl.textContent = char.specialTrait || char.role;
  hpTextEl.textContent = `${char.maxHealth} / ${char.maxHealth}`;
  hpFillEl.style.width = '100%';
  hpFillEl.className = 'hp-bar-fill';
  atkEl.textContent = char.attackPower;
  defEl.textContent = char.defense;
  roleEl.textContent = char.role;
  roleEl.className = `fighter-role-badge ${getRoleClass(char.role)}`;
}

function prepareFighterForArena(charName) {
  window.soundEngine?.playClick();
  // Switch to arena tab
  const arenaTab = document.querySelector('.nav-tab[data-tab="arenaTab"]');
  if (arenaTab) arenaTab.click();

  const select1 = document.getElementById('fighter1Select');
  const select2 = document.getElementById('fighter2Select');

  if (!select1.value || select1.value === charName) {
    select1.value = charName;
    selectFighter(1, charName);
  } else {
    select2.value = charName;
    selectFighter(2, charName);
  }
}

function randomizeMatchup() {
  window.soundEngine?.playClick();
  if (state.characters.length < 2) return;

  const shuffled = [...state.characters].sort(() => 0.5 - Math.random());
  const f1 = shuffled[0];
  const f2 = shuffled[1];

  document.getElementById('fighter1Select').value = f1.name;
  document.getElementById('fighter2Select').value = f2.name;
  selectFighter(1, f1.name);
  selectFighter(2, f2.name);
}

function validateBattleReady() {
  const btn = document.getElementById('commenceBattleBtn');
  if (!btn) return;

  const ready = state.selectedFighter1 && state.selectedFighter2 && 
                state.selectedFighter1.name !== state.selectedFighter2.name;

  btn.disabled = !ready || state.isBattleInProgress;
}

/* ==========================================================================
   TURN-BY-TURN BATTLE SIMULATION & VISUAL PLAYBACK
   ========================================================================== */
async function startBattleSimulation() {
  if (state.isBattleInProgress) return;
  if (!state.selectedFighter1 || !state.selectedFighter2) return;
  if (state.selectedFighter1.name === state.selectedFighter2.name) {
    showToast('A pirate cannot fight themselves!', 'error');
    return;
  }

  state.isBattleInProgress = true;
  validateBattleReady();
  window.soundEngine?.playClick();

  // Reset HP Bars
  updateFighterPreview(1, state.selectedFighter1);
  updateFighterPreview(2, state.selectedFighter2);

  const theater = document.getElementById('battleTheater');
  const terminal = document.getElementById('combatTerminal');
  const victoryCard = document.getElementById('victoryCard');

  theater.classList.add('active');
  victoryCard.classList.remove('active');
  terminal.innerHTML = `<div class="terminal-line">⚡ Connecting to Tomcat Battle Arena Engine...</div>`;

  try {
    const res = await fetch('api/battle', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        fighter1: state.selectedFighter1.name,
        fighter2: state.selectedFighter2.name
      })
    });

    const data = await res.json();
    if (!res.ok) {
      throw new Error(data.message || 'Battle simulation failed.');
    }

    const battleResult = data.data;
    await playbackBattleRounds(battleResult);

    // Refresh history
    fetchHistory();

  } catch (err) {
    showToast('Battle error: ' + err.message, 'error');
    state.isBattleInProgress = false;
    validateBattleReady();
  }
}

async function playbackBattleRounds(battleResult) {
  const rounds = battleResult.rounds || [];
  const terminal = document.getElementById('combatTerminal');
  const roundIndicator = document.getElementById('roundIndicatorPill');
  const roundTitle = document.getElementById('roundActionTitle');
  const roundDesc = document.getElementById('roundActionDesc');

  terminal.innerHTML += `<div class="terminal-line" style="color: var(--gold-glow)">🏴‍☠️ BATTLE COMMENCED: ${escapeHtml(battleResult.combatant1Name)} VS ${escapeHtml(battleResult.combatant2Name)}</div>`;

  for (let i = 0; i < rounds.length; i++) {
    const r = rounds[i];
    roundIndicator.textContent = `ROUND ${r.round} OF ${battleResult.totalRounds}`;

    // Highlight active fighter card
    const isF1Attacking = r.attacker.toLowerCase() === state.selectedFighter1.name.toLowerCase();
    const attackerCard = document.getElementById(isF1Attacking ? 'fighter1Card' : 'fighter2Card');
    const defenderCard = document.getElementById(isF1Attacking ? 'fighter2Card' : 'fighter1Card');

    attackerCard.classList.add('active-turn');
    defenderCard.classList.remove('active-turn');

    // Sound effect & tags
    let actionBadge = '';
    if (r.critical) {
      actionBadge = `<span class="crit-tag">CRITICAL HIT!</span>`;
      window.soundEngine?.playCrit();
    } else if (r.haki) {
      actionBadge = `<span class="haki-tag">HAKI</span>`;
      window.soundEngine?.playHaki();
    } else {
      window.soundEngine?.playAttack();
    }

    roundTitle.innerHTML = `${escapeHtml(r.attacker)}: ${escapeHtml(r.actionType)} ${actionBadge}`;
    roundDesc.textContent = `${r.description} (Damage dealt: ${r.damage})`;

    // Update HP Bars
    if (isF1Attacking) {
      applyHpUpdate(2, r.defenderHp, r.defenderMaxHp);
    } else {
      applyHpUpdate(1, r.defenderHp, r.defenderMaxHp);
    }

    // Terminal Line
    const termClass = r.critical ? 'crit' : (r.haki ? 'haki' : '');
    terminal.innerHTML += `<div class="terminal-line ${termClass}">[Round ${r.round}] ${escapeHtml(r.attacker)} deal ${r.damage} dmg to ${escapeHtml(r.defender)} &rarr; ${r.defender} HP: ${r.defenderHp}/${r.defenderMaxHp}</div>`;
    terminal.scrollTop = terminal.scrollHeight;

    // Await playback delay if not skipped
    if (state.playbackSpeed > 0) {
      await delay(state.playbackSpeed);
    }
  }

  // Combat Finished
  document.getElementById('fighter1Card').classList.remove('active-turn');
  document.getElementById('fighter2Card').classList.remove('active-turn');

  // Mark loser card defeated
  const isF1Winner = battleResult.winnerName.toLowerCase() === state.selectedFighter1.name.toLowerCase();
  document.getElementById(isF1Winner ? 'fighter2Card' : 'fighter1Card').classList.add('defeated');

  // Victory Fanfare
  window.soundEngine?.playVictory();

  terminal.innerHTML += `<div class="terminal-line winner">🏆 MATCH CONCLUDED: ${escapeHtml(battleResult.winnerName)} is Victorious!</div>`;
  terminal.scrollTop = terminal.scrollHeight;

  // Show Victory Card
  const victoryCard = document.getElementById('victoryCard');
  document.getElementById('victoryWinnerName').textContent = battleResult.winnerName;
  document.getElementById('victorySummaryText').textContent = battleResult.summary;
  victoryCard.classList.add('active');

  state.isBattleInProgress = false;
  validateBattleReady();
}

function applyHpUpdate(slot, currentHp, maxHp) {
  const hpFill = document.getElementById(`fighter${slot}HpFill`);
  const hpText = document.getElementById(`fighter${slot}HpText`);
  if (!hpFill || !hpText) return;

  const pct = Math.max(0, Math.min(100, Math.round((currentHp / maxHp) * 100)));
  hpFill.style.width = pct + '%';
  hpText.textContent = `${currentHp} / ${maxHp}`;

  if (pct > 50) {
    hpFill.className = 'hp-bar-fill';
  } else if (pct > 20) {
    hpFill.className = 'hp-bar-fill warning';
  } else {
    hpFill.className = 'hp-bar-fill danger';
  }
}

/* ==========================================================================
   CREWS FLEET MANAGEMENT
   ========================================================================== */
function renderCrewsGrid(crews) {
  const grid = document.getElementById('crewsFleetGrid');
  if (!grid) return;

  if (!crews || crews.length === 0) {
    grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: var(--text-dim); padding: 3rem;">No pirate fleets registered yet.</div>`;
    return;
  }

  grid.innerHTML = crews.map(crew => {
    const memberRows = (crew.members || []).map(m => `
      <div class="crew-member-row">
        <div class="member-info">
          <span>${getCharacterAvatar(m)}</span>
          <strong>${escapeHtml(m.name)}</strong>
          <span style="font-size: 0.75rem; color: var(--text-dim)">(${m.role})</span>
        </div>
        <div>
          <button class="member-remove-btn" title="Remove Member" onclick="removeCrewMember('${escapeHtml(crew.crewName)}', '${escapeHtml(m.name)}')">&times;</button>
        </div>
      </div>
    `).join('') || '<div style="font-size: 0.8rem; color: var(--text-dim)">No members enlisted.</div>';

    return `
      <div class="crew-card">
        <div class="crew-header">
          <div class="crew-name-title">⚓ ${escapeHtml(crew.crewName)}</div>
          <span class="tab-badge">${crew.memberCount} Pirates</span>
        </div>
        <div class="crew-members-list">
          ${memberRows}
        </div>
        <div>
          <button class="btn-card-action" onclick="openAddMemberModal('${escapeHtml(crew.crewName)}')">+ Enlist Member</button>
        </div>
      </div>
    `;
  }).join('');
}

function populateCrewSelectOptions() {
  const select = document.getElementById('newMemberCrewSelect');
  if (!select) return;
  select.innerHTML = state.crews.map(c => `<option value="${escapeHtml(c.crewName)}">${escapeHtml(c.crewName)}</option>`).join('');
}

async function removeCrewMember(crewName, characterName) {
  if (!confirm(`Remove ${characterName} from ${crewName}?`)) return;

  try {
    const res = await fetch(`api/crews?crewName=${encodeURIComponent(crewName)}&characterName=${encodeURIComponent(characterName)}`, {
      method: 'DELETE'
    });
    const data = await res.json();
    if (res.ok) {
      showToast(`Removed ${characterName} from ${crewName}`, 'success');
      window.soundEngine?.playClick();
      await fetchCrews();
    } else {
      showToast(data.message || 'Error removing member', 'error');
    }
  } catch (err) {
    showToast('Failed to remove crew member: ' + err.message, 'error');
  }
}

/* ==========================================================================
   CHRONICLES (BATTLE HISTORY)
   ========================================================================== */
function renderHistoryList(data) {
  const container = document.getElementById('historyListContainer');
  if (!container) return;

  const summaries = data.summaries || [];
  if (summaries.length === 0) {
    container.innerHTML = `<div style="text-align: center; color: var(--text-dim); padding: 3rem;">No battles recorded yet in the chronicles archive.</div>`;
    return;
  }

  container.innerHTML = summaries.map(sum => `
    <div class="history-item">
      <div class="history-matchup">📜 ${escapeHtml(sum)}</div>
    </div>
  `).reverse().join('');
}

async function clearBattleHistory() {
  if (!confirm('Are you sure you want to purge all archived battle chronicles?')) return;

  try {
    const res = await fetch('api/history', { method: 'DELETE' });
    const data = await res.json();
    if (res.ok) {
      showToast('Battle chronicle archive purged.', 'success');
      window.soundEngine?.playClick();
      fetchHistory();
    } else {
      showToast(data.message || 'Error clearing history', 'error');
    }
  } catch (err) {
    showToast('Failed to clear history: ' + err.message, 'error');
  }
}

/* ==========================================================================
   POLYMORPHISM LAB
   ========================================================================== */
async function runPolymorphismDemo() {
  const terminal = document.getElementById('labTerminal');
  if (!terminal) return;

  terminal.innerHTML = '⚡ Executing Java OOP Polymorphism dynamic dispatch...';

  try {
    const res = await fetch('api/demo');
    const data = await res.json();
    if (!res.ok) throw new Error(data.message);

    const steps = data.demonstrationSteps || [];
    terminal.innerHTML = steps.map(s => `
      <div style="margin-bottom: 1rem; border-left: 2px solid var(--gold-primary); padding-left: 0.75rem;">
        <strong style="color: var(--gold-glow); font-size: 0.95rem;">${escapeHtml(s.title)}</strong>
        <div style="color: var(--text-muted); font-size: 0.8rem; margin-bottom: 0.25rem;">[OOP Concept] ${escapeHtml(s.concept)}</div>
        <div style="color: #cbd5e1; background: rgba(255,255,255,0.03); padding: 0.4rem 0.6rem; border-radius: 4px;">&gt; ${escapeHtml(s.output)}</div>
      </div>
    `).join('');

  } catch (err) {
    terminal.innerHTML = `<span style="color: var(--crimson-red)">Error running demo: ${escapeHtml(err.message)}</span>`;
  }
}

/* ==========================================================================
   MODALS & FORMS
   ========================================================================== */
function initModals() {
  // Recruit Character Modal
  const recruitModal = document.getElementById('recruitModal');
  const openRecruitBtn = document.getElementById('openRecruitModalBtn');
  const closeRecruitBtn = document.getElementById('closeRecruitModalBtn');
  const roleSelect = document.getElementById('newCharRole');

  openRecruitBtn?.addEventListener('click', () => {
    window.soundEngine?.playClick();
    recruitModal?.classList.add('active');
  });

  closeRecruitBtn?.addEventListener('click', () => {
    recruitModal?.classList.remove('active');
  });

  roleSelect?.addEventListener('change', (e) => {
    updateDynamicRoleFields(e.target.value);
  });

  document.getElementById('recruitForm')?.addEventListener('submit', handleRecruitSubmit);

  // Create Crew Modal
  const createCrewModal = document.getElementById('createCrewModal');
  document.getElementById('openCreateCrewModalBtn')?.addEventListener('click', () => {
    window.soundEngine?.playClick();
    createCrewModal?.classList.add('active');
  });
  document.getElementById('closeCreateCrewModalBtn')?.addEventListener('click', () => {
    createCrewModal?.classList.remove('active');
  });
  document.getElementById('createCrewForm')?.addEventListener('submit', handleCreateCrewSubmit);

  // Add Member Modal
  const addMemberModal = document.getElementById('addMemberModal');
  document.getElementById('closeAddMemberModalBtn')?.addEventListener('click', () => {
    addMemberModal?.classList.remove('active');
  });
  document.getElementById('addMemberForm')?.addEventListener('submit', handleAddMemberSubmit);
}

function updateDynamicRoleFields(role) {
  const container = document.getElementById('dynamicRoleFields');
  if (!container) return;

  switch (role) {
    case 'Captain':
      container.innerHTML = `
        <div class="form-group">
          <label class="form-label">Affiliated Crew Name</label>
          <input type="text" id="newCharCrew" class="form-input" placeholder="e.g. StrawHat Pirates" required>
        </div>
      `;
      break;
    case 'Swordsman':
      container.innerHTML = `
        <div class="form-group">
          <label class="form-label">Blades / Sword Count</label>
          <input type="number" id="newCharSwords" class="form-input" min="1" max="9" value="3" required>
        </div>
      `;
      break;
    case 'Navigator':
      container.innerHTML = `
        <div class="form-group">
          <label class="form-label">Navigation Tool</label>
          <input type="text" id="newCharTool" class="form-input" placeholder="e.g. Perfect Clima-Tact" required>
        </div>
      `;
      break;
    case 'DevilFruitUser':
      container.innerHTML = `
        <div class="form-group">
          <label class="form-label">Devil Fruit Name</label>
          <input type="text" id="newCharFruitName" class="form-input" placeholder="e.g. Gomu Gomu no Mi" required>
        </div>
        <div class="form-group">
          <label class="form-label">Devil Fruit Classification</label>
          <select id="newCharFruitType" class="form-select">
            <option value="Paramecia">Paramecia</option>
            <option value="Zoan">Zoan</option>
            <option value="Logia">Logia</option>
            <option value="Mythical Zoan">Mythical Zoan</option>
            <option value="Special Paramecia">Special Paramecia</option>
          </select>
        </div>
      `;
      break;
    default:
      container.innerHTML = '';
  }
}

async function handleRecruitSubmit(e) {
  e.preventDefault();
  const role = document.getElementById('newCharRole').value;
  const name = document.getElementById('newCharName').value.trim();
  const health = parseInt(document.getElementById('newCharHp').value);
  const attackPower = parseInt(document.getElementById('newCharAtk').value);
  const defense = parseInt(document.getElementById('newCharDef').value);

  const payload = { role, name, health, attackPower, defense };

  if (role === 'Captain') {
    payload.crewName = document.getElementById('newCharCrew')?.value.trim();
  } else if (role === 'Swordsman') {
    payload.swordCount = document.getElementById('newCharSwords')?.value.trim();
  } else if (role === 'Navigator') {
    payload.navigationTool = document.getElementById('newCharTool')?.value.trim();
  } else if (role === 'DevilFruitUser') {
    payload.devilFruitName = document.getElementById('newCharFruitName')?.value.trim();
    payload.devilFruitType = document.getElementById('newCharFruitType')?.value;
  }

  try {
    const res = await fetch('api/characters', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await res.json();
    if (res.ok) {
      showToast(`Recruited ${name} to the roster!`, 'success');
      window.soundEngine?.playClick();
      document.getElementById('recruitModal')?.classList.remove('active');
      document.getElementById('recruitForm')?.reset();
      await loadAllData();
    } else {
      showToast(data.message || 'Validation error', 'error');
    }
  } catch (err) {
    showToast('Failed to recruit character: ' + err.message, 'error');
  }
}

async function handleCreateCrewSubmit(e) {
  e.preventDefault();
  const crewName = document.getElementById('newCrewNameInput').value.trim();
  if (!crewName) return;

  try {
    const res = await fetch('api/crews', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ action: 'create', crewName })
    });

    const data = await res.json();
    if (res.ok) {
      showToast(`Crew '${crewName}' registered!`, 'success');
      window.soundEngine?.playClick();
      document.getElementById('createCrewModal')?.classList.remove('active');
      document.getElementById('createCrewForm')?.reset();
      fetchCrews();
    } else {
      showToast(data.message || 'Error creating crew', 'error');
    }
  } catch (err) {
    showToast('Failed to create crew: ' + err.message, 'error');
  }
}

function openAddMemberModal(crewName) {
  window.soundEngine?.playClick();
  const modal = document.getElementById('addMemberModal');
  const select = document.getElementById('newMemberCrewSelect');
  if (select) select.value = crewName;

  // Populate candidate characters
  const charSelect = document.getElementById('newMemberCharSelect');
  if (charSelect) {
    charSelect.innerHTML = state.characters.map(c => 
      `<option value="${escapeHtml(c.name)}">${getCharacterAvatar(c)} ${escapeHtml(c.name)}</option>`
    ).join('');
  }

  modal?.classList.add('active');
}

async function handleAddMemberSubmit(e) {
  e.preventDefault();
  const crewName = document.getElementById('newMemberCrewSelect').value;
  const characterName = document.getElementById('newMemberCharSelect').value;

  try {
    const res = await fetch('api/crews', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ action: 'addMember', crewName, characterName })
    });

    const data = await res.json();
    if (res.ok) {
      showToast(`Enlisted ${characterName} into ${crewName}!`, 'success');
      window.soundEngine?.playClick();
      document.getElementById('addMemberModal')?.classList.remove('active');
      fetchCrews();
    } else {
      showToast(data.message || 'Error adding member', 'error');
    }
  } catch (err) {
    showToast('Failed to add member: ' + err.message, 'error');
  }
}

/* ==========================================================================
   TOAST NOTIFICATION SYSTEM & UTILITIES
   ========================================================================== */
function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.textContent = message;

  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}
