package thesis.cozy.lobsters;

import thesis.common.IntMap;
import thesis.common.IntSet;

import java.util.NoSuchElementException;

public final class Lobsters {

    private IntMap<Story> stories;
    private IntMap<Vote> votes;

    private Lobsters() {
        this.stories = IntMap.empty();
        this.votes = IntMap.empty();
    }

    public static Lobsters create() {
        return new Lobsters();
    }

    public void insertVote(int voteId, int userId, int commentId) {
        if (IntMap.containsKey(votes, voteId))
            throw new IllegalArgumentException("duplicated vote: " + voteId);
        votes = IntMap.put(votes, voteId, new Vote(userId, commentId));
    }

    public void insertStory(int storyId, int mergedId, boolean isExpired, int createdAt) {
        if (IntMap.containsKey(stories, storyId))
            throw new IllegalArgumentException("duplicated story: " + storyId);
        Story story = new Story(mergedId, isExpired, createdAt,
                IntSet.empty(), IntSet.empty(), IntSet.empty());
        stories = IntMap.put(stories, storyId, story);
    }

    public void hideStoryFromUser(int storyId, int userId) {
        Story oldStory = IntMap.getOrThrow(stories, storyId);
        Story newStory = new Story(oldStory.mergedId, oldStory.isExpired, oldStory.createdAt,
                IntSet.add(oldStory.hiddenToUsers, userId),
                oldStory.tags,
                oldStory.votes);
        stories = IntMap.put(stories, storyId, newStory);
    }

    public void addStoryTag(int storyId, int tagId) {
        Story oldStory = IntMap.getOrThrow(stories, storyId);
        Story newStory = new Story(oldStory.mergedId, oldStory.isExpired, oldStory.createdAt,
                oldStory.hiddenToUsers,
                IntSet.add(oldStory.tags, tagId),
                oldStory.votes);
        stories = IntMap.put(stories, storyId, newStory);
    }

    public void addStoryVote(int storyId, int voteId) {
        if (!IntMap.containsKey(votes, voteId))
            throw new IllegalArgumentException("unknown vote: " + voteId);
        Story oldStory = IntMap.getOrThrow(stories, storyId);
        Story newStory = new Story(oldStory.mergedId, oldStory.isExpired, oldStory.createdAt,
                oldStory.hiddenToUsers,
                oldStory.tags,
                IntSet.add(oldStory.votes, voteId));
        stories = IntMap.put(stories, storyId, newStory);
    }

    private void print() {
        for (IntMap<Story> curr = stories; curr != null; curr = curr.next) {
            int storyId = curr.key;
            Story story = curr.value;
            System.out.printf("Story$%d: mergedId=%d, isExpired=%b, createdAt=%d, " +
                              "hiddenToUsers=%s, tags=%s, votes=%s\n",
                    storyId, story.mergedId, story.isExpired, story.createdAt,
                    IntSet.toString(story.hiddenToUsers),
                    IntSet.toString(story.tags),
                    IntSet.toString(story.votes));
        }
        for (IntMap<Vote> curr = votes; curr != null; curr = curr.next) {
            int voteId = curr.key;
            Vote vote = curr.value;
            System.out.printf("Vote$%d: userId=%d, commentId=%d\n",
                    voteId, vote.userId, vote.commentId);
        }
        System.out.println("==========");
    }

    public static void main(String[] args) {
        Lobsters lobsters = Lobsters.create();
        lobsters.insertVote(1, 100, 200);
        lobsters.insertVote(2, 101, 201);
        lobsters.insertStory(11, 5001, true, 20250701);
        lobsters.insertStory(10, 5000, false, 20260101);
        lobsters.hideStoryFromUser(10, 100);
        lobsters.hideStoryFromUser(10, 100);
        lobsters.addStoryTag(10, 300);
        lobsters.addStoryTag(10, 0);
        lobsters.addStoryVote(11, 2);
        lobsters.print();
        try {
            lobsters = Lobsters.create();
            lobsters.insertVote(1, 100, 200);
            lobsters.insertVote(1, 100, 200);
            System.out.println("ERROR: duplicated vote not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            lobsters = Lobsters.create();
            lobsters.insertStory(10, 5000, false, 20260101);
            lobsters.insertStory(10, 5000, false, 20260101);
            System.out.println("ERROR: duplicated story not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            lobsters = Lobsters.create();
            lobsters.insertStory(10, 5000, false, 20260101);
            lobsters.addStoryVote(10, 1);
            System.out.println("ERROR: unknown vote not detected");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            lobsters = Lobsters.create();
            lobsters.hideStoryFromUser(10, 100);
            System.out.println("ERROR: unknown story not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            lobsters = Lobsters.create();
            lobsters.addStoryTag(10, 300);
            System.out.println("ERROR: unknown story not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        try {
            lobsters = Lobsters.create();
            lobsters.insertVote(1, 100, 200);
            lobsters.addStoryVote(10, 1);
            System.out.println("ERROR: unknown story not detected");
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
    }

}
