package thesis.cozy.lobsters;

import thesis.common.ValueType;

public final class Vote extends ValueType {

    public final int userId;
    public final int commentId;

    public Vote(int userId, int commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }

}
