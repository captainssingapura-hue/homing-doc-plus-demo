function createJumpPhysics(gravity, jumpStrength) {
    let vy = 0;
    let jumping = false;
    let g = gravity;

    return {
        jump() {
            if (!jumping) {
                vy = -jumpStrength;
                jumping = true;
            }
        },
        fall() {
            if (!jumping) {
                vy = 0;
                jumping = true;
            }
        },
        setGravity(value) {
            g = value;
        },
        update(y, groundY) {
            if (!jumping) return y;
            vy += g;
            y += vy;
            if (y >= groundY) {
                y = groundY;
                vy = 0;
                jumping = false;
            }
            return y;
        },
        isJumping() {
            return jumping;
        },
        getVy() {
            return vy;
        },
        // RFC 0029 / Widgets Are State Functions — re-seat physics from a
        // saved snapshot. evalState's job: same (vy, jumping) ⇒ same next
        // frame as the session that captured them.
        restore(savedVy, savedJumping) {
            vy = (typeof savedVy === "number") ? savedVy : 0;
            jumping = !!savedJumping;
        }
    };
}
