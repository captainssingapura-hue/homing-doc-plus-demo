// =============================================================================
// MinesweeperGame — the RFC 0050 Appendix A boundary case as a pure game
// domain: createMinesweeperGame(config) returns { adapter, act, reset }.
//
// The board is a Relation of display symbols (pks r0..rN, columns c0..cN);
// the grid renders it read-only (editable:false) and hands every action key
// to act(key, pk, col) through the grid's onAction contract. Reveals, flags,
// chords, and restarts all flow back through the adapter feed — the grid's
// DIRECT (PK,col) update path — so a flood fill batches into one frame.
//
// Keymap (the appendix): Enter / Space reveal · F flag · C chord · R restart.
// =============================================================================

function createMinesweeperGame(config) {
    config = config || {};
    var N = config.size || 9;
    var MINES = config.mines || 10;
    var onStatus = config.onStatus || function () {};
    var onProgress = config.onProgress || function () {};

    var state, over, revealed, subs = [];

    function freshState() {
        state = {}; over = false; revealed = 0;
        for (var r = 0; r < N; r++) { state[r] = {};
            for (var c = 0; c < N; c++) state[r][c] = { mine: false, adj: 0, revealed: false, flagged: false }; }
        var placed = 0;
        while (placed < MINES) {
            var r2 = Math.floor(Math.random() * N), c2 = Math.floor(Math.random() * N);
            if (!state[r2][c2].mine) { state[r2][c2].mine = true; placed++; }
        }
        forEachCell(function (r3, c3) {
            state[r3][c3].adj = neighbours(r3, c3)
                .filter(function (p) { return state[p[0]][p[1]].mine; }).length;
        });
    }

    function forEachCell(fn) {
        for (var r = 0; r < N; r++) for (var c = 0; c < N; c++) fn(r, c);
    }

    function neighbours(r, c) {
        var out = [];
        for (var dr = -1; dr <= 1; dr++) for (var dc = -1; dc <= 1; dc++) {
            if (!dr && !dc) continue;
            var rr = r + dr, cc = c + dc;
            if (rr >= 0 && rr < N && cc >= 0 && cc < N) out.push([rr, cc]);
        }
        return out;
    }

    function symbol(s) {
        if (s.flagged && !s.revealed) return '🚩';
        if (!s.revealed) return '·';
        if (s.mine) return '💥';
        return s.adj ? String(s.adj) : ' ';
    }

    function push(r, c) {
        var v = symbol(state[r][c]);
        subs.slice().forEach(function (fn) { fn('r' + r, 'c' + c, v); });
    }

    var adapter = {
        pks:     function () { var out = []; for (var r = 0; r < N; r++) out.push('r' + r); return out; },
        columns: function () { var out = []; for (var c = 0; c < N; c++) out.push('c' + c); return out; },
        get:     function (pk, col) { return symbol(state[pk.slice(1)][col.slice(1)]); },
        subscribe:   function (fn) { subs.push(fn); },
        unsubscribe: function (fn) { var i = subs.indexOf(fn); if (i >= 0) subs.splice(i, 1); }
    };

    function reveal(r, c) {
        var s = state[r][c];
        if (s.revealed || s.flagged) return 0;
        s.revealed = true;
        push(r, c);
        if (s.mine) return -1;
        var n = 1;
        if (s.adj === 0) neighbours(r, c).forEach(function (p) {
            var got = reveal(p[0], p[1]);
            if (got > 0) n += got;
        });
        return n;
    }

    function boom() {
        over = true;
        forEachCell(function (r, c) {
            if (state[r][c].mine) { state[r][c].revealed = true; push(r, c); }
        });
        onStatus('💥 BOOM — press R to restart');
    }

    function afterReveal(got) {
        if (got < 0) { boom(); return; }
        if (got > 0) {
            revealed += got;
            onProgress(got + ' revealed in one flood fill → one frame; '
                     + revealed + '/' + (N * N - MINES) + ' clear');
            if (revealed === N * N - MINES) { over = true; onStatus('🏁 You win! Press R for a new board'); }
        }
    }

    /** Chord: on a satisfied number, reveal every unflagged neighbour. */
    function chord(r, c) {
        var s = state[r][c];
        if (!s.revealed || !s.adj) return;
        var around = neighbours(r, c);
        var flags = around.filter(function (p) { return state[p[0]][p[1]].flagged; }).length;
        if (flags !== s.adj) return;
        var total = 0, hit = false;
        around.forEach(function (p) {
            if (hit || state[p[0]][p[1]].flagged) return;
            var got = reveal(p[0], p[1]);
            if (got < 0) hit = true; else total += got;
        });
        afterReveal(hit ? -1 : total);
    }

    function reset() {
        freshState();
        forEachCell(push);
        onStatus('');
        onProgress('');
    }

    /** The grid's onAction target: (key, pk, column) → game move. */
    function act(key, pk, col) {
        if (key === 'r' || key === 'R') { reset(); return; }
        if (over) return;
        var r = parseInt(pk.slice(1), 10), c = parseInt(col.slice(1), 10);
        if (key === 'Enter' || key === ' ') afterReveal(reveal(r, c));
        else if (key === 'f' || key === 'F') {
            var s = state[r][c];
            if (!s.revealed) { s.flagged = !s.flagged; push(r, c); }
        }
        else if (key === 'c' || key === 'C') chord(r, c);
    }

    freshState();
    return { adapter: adapter, act: act, reset: reset, size: N };
}
