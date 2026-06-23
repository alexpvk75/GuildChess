import io
import math
import chess
import chess.pgn

def getMaterial(board, color):
    PIECE_VALUES = {
        chess.PAWN: 1, chess.KNIGHT: 3, chess.BISHOP: 3,
        chess.ROOK: 5, chess.QUEEN: 9, chess.KING: 0
    }
    return sum(
        PIECE_VALUES[pt] * len(board.pieces(pt, color))
        for pt in PIECE_VALUES
    ) / 39


def getMobility(board, color):
    if board.turn == color:
        m = board.legal_moves.count()
    else:
        b = board.copy(stack=False)
        b.turn = color
        m = b.legal_moves.count()
    return min(1.0, math.log(1+m) / math.log(1+120))

def getCenterControl(board, color):
    CENTER = [chess.D4, chess.E4, chess.D5, chess.E5]
    score = 0.0
    k = 2

    for sq in CENTER:
        attackers = len(board.attackers(color, sq))
        score += attackers / (attackers + k)

    return score / 4

def getKingSafety(board, color):
    king_square = board.king(color)
    if king_square is None:
        return 0.0
    enemy_score = 0.0
    friendly_score = 0.0
    k = 2
    count = 0
    for sq in chess.SQUARES:
        if chess.square_distance(sq, king_square) <= 2:
            count+=1
            enemy_attackers = len(board.attackers(not color, sq))
            friendly_defenders = len(board.attackers(color, sq))
            enemy_score += enemy_attackers / (enemy_attackers + k)
            friendly_score += friendly_defenders / (friendly_defenders + k)
    enemy_score /= count
    friendly_score /= count
    safety = (1.0 - enemy_score + 0.5 * friendly_score)/1.5
    if board.is_check() and board.turn == color:
        safety -= 0.2
    if board.is_checkmate() and board.turn == color:
        return 0.0
    return max(0.0, min(1.0, safety))

def getTropism(board, color):
    PIECE_WEIGHTS = {
        chess.PAWN: 1, chess.KNIGHT: 3, chess.BISHOP: 3,
        chess.ROOK: 5, chess.QUEEN: 9
    }
    king_square = board.king(color)
    if king_square is None:
        return 1.0

    enemy = not color
    raw = 0.0
    for pt, weight in PIECE_WEIGHTS.items():
        for sq in board.pieces(pt, enemy):
            dist = max(1, chess.square_distance(sq, king_square))
            raw += weight / dist

    return min(1.0, raw / 39.0)

def evaluate(board):
    def phase(board):
        nQueen = len(board.pieces(chess.QUEEN,  chess.WHITE)) + len(board.pieces(chess.QUEEN,  chess.BLACK))
        nRook = len(board.pieces(chess.ROOK,   chess.WHITE)) + len(board.pieces(chess.ROOK,   chess.BLACK))
        nBishop = len(board.pieces(chess.BISHOP, chess.WHITE)) + len(board.pieces(chess.BISHOP, chess.BLACK))
        nKnight = len(board.pieces(chess.KNIGHT, chess.WHITE)) + len(board.pieces(chess.KNIGHT, chess.BLACK))
        phase = 4 * nQueen + 2 * nRook + nBishop + nKnight
        return max(0.0, min(1.0, phase / 24.0))
    o = phase(board)
    e = 1 - o
    def score(color):
        material = getMaterial(board, color)
        mobility = getMobility(board, color)
        center = getCenterControl(board, color)
        king_safety = getKingSafety(board, color)
        tropism = getTropism(board, color)

        w_material = 0.30 * o + 0.5 * e
        w_mobility = 0.20
        w_center = 0.20 * o
        w_king_safety = 0.20 * o + 0.10 * e
        w_tropism = 0.10 * o + 0.20 * e

        total = w_material + w_mobility + w_center + w_king_safety + w_tropism
        return (
            w_material * material +
            w_mobility * mobility +
            w_center * center +
            w_king_safety * king_safety +
            w_tropism * tropism
        ) / total
    if board.is_checkmate():
        if board.turn == chess.WHITE:
            return -1000
        else:
            return 1000
    if (board.is_stalemate() or
        board.is_insufficient_material() or
        board.is_seventyfive_moves() or
        board.is_fivefold_repetition()):
        return 0.0
    return score(chess.WHITE) - score(chess.BLACK)

TT = {}

def ordered_moves(board):
    return sorted(
        board.legal_moves,
        key=lambda m: (
            board.is_capture(m),
            board.gives_check(m)
        ),
        reverse=True
    )


def alphabeta(board, depth, alpha, beta):
    key = (board.fen(), depth)

    if key in TT:
        return TT[key]

    if depth == 0 or board.is_game_over():
        value = evaluate(board)
        TT[key] = value
        return value

    if board.turn == chess.WHITE:
        value = -float("inf")
        for move in ordered_moves(board):
            board.push(move)
            value = max(
                value,
                alphabeta(board, depth - 1, alpha, beta)
            )
            board.pop()
            alpha = max(alpha, value)
            if alpha >= beta:
                break
    else:
        value = float("inf")
        for move in ordered_moves(board):
            board.push(move)
            value = min(
                value,
                alphabeta(board, depth - 1, alpha, beta)
            )
            board.pop()

            beta = min(beta, value)

            if alpha >= beta:
                break
    TT[key] = value
    return value

pgn = io.StringIO("""
1. e4 c5 2. Qh5 Nf6 3. Qxc5 Nxe4 4. Qe3 d5 5. Bd3 e6 6. Bb5+ Bd7 7. Qb3 Bc5 8.
d3 Nd6 9. Nc3 O-O 10. Nf3 a6 11. Bxd7 Nxd7 12. Bf4 Qc7 13. Bxd6 Qxd6 14. O-O b5
15. a3 Rab8 16. Qa2 d4 17. Ne4 Qf4 18. Nxc5 Nxc5 19. b4 Na4 20. Qb1 Nc3 21. Qc1
Ne2+ 22. Kh1 Nxc1 23. Raxc1 Rbc8 24. Ne1 Qxc1 25. g3 Rc3 26. Kg2 Rxa3 27. Nf3
Qxc2 28. Nxd4 Qxd3 29. Nc6 Re8 30. Ne5 Qe4+ 31. Kh3 Qxe5 32. f4 Qe2 33. Rg1 e5
34. f5 Qf3 35. Rg2 Qxf5+ 36. Kh4 g5+ 37. Kh5 Qh3+ 38. Kxg5 Qxg2 39. g4 Re6 40.
h4 Qd2+ 41. Kf5 Qf4#
""")

game = chess.pgn.read_game(pgn)

board = game.board()

accuracyWhite = []
accuracyBlack = []

DEPTH = 2

for i, move in enumerate(game.mainline_moves(), start=1):
    player = board.turn
    if player == chess.WHITE:
        best_eval = -float("inf")
        for candidate in board.legal_moves:
            board.push(candidate)
            score = alphabeta(board, DEPTH - 1,
                               -float("inf"), float("inf"))
            board.pop()
            best_eval = max(best_eval, score)

    else:
        best_eval = float("inf")
        for candidate in board.legal_moves:
            board.push(candidate)
            score = alphabeta(board, DEPTH - 1,
                               -float("inf"), float("inf"))
            board.pop()
            best_eval = min(best_eval, score)
    san = board.san(move)
    board.push(move)
    CAP = 1.0
    best_eval = max(-CAP, min(CAP, best_eval))
    actual_eval = alphabeta(board, DEPTH - 1,
                            -float("inf"), float("inf"))
    actual_eval = max(-CAP, min(CAP, actual_eval))
    print("Eval: ", actual_eval)
    delta = abs(best_eval - actual_eval)
    acc = math.exp(-7 * delta)

    if player == chess.WHITE:
        accuracyWhite.append(acc)
    else:
        accuracyBlack.append(acc)

whiteAccuracy = 100 * sum(accuracyWhite) / len(accuracyWhite)
blackAccuracy = 100 * sum(accuracyBlack) / len(accuracyBlack)

print(f"White accuracy: {whiteAccuracy:.2f}%")
print(f"Black accuracy: {blackAccuracy:.2f}%")