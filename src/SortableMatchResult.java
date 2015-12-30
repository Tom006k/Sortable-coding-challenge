/**
*** SortableMatchResult class
***	Defines a matcher result for SortableProductMatcher.
**/

public class SortableMatchResult {
	/**
	*** MatchType Enum
	*** The type of match.
	**/
	public enum MatchType {
		///Indicates there is no match.
		NO_MATCH,
		///Indicates a full match i.e. equal to.
		FULL_MATCH,
		///Indicates the subject starts with the value followed by a whitespace.
		STARTS_WITH_MATCH,
		///Indicates the subject contains the value with a whitespace or end-of-string delimiter at either end.
		CONTAINS_MATCH,
		///Indicates a FULL_MATCH for a partial test or value.
		FULL_PARTIAL_MATCH,
		///Indicates a STARTS_WITH_MATCH for a partial test or value.
		STARTS_WITH_PARTIAL_MATCH,
		///Indicates a CONTAINS_MATCH for a partial test or value.
		CONTAINS_PARTIAL_MATCH
	};
	///The match type.
	private MatchType matchType;
	/** Constructs a new SortableMatchResult. **/
	public SortableMatchResult() {
	}
	/** Constructs a new SortableMatchResult of the given match type. **/
	public SortableMatchResult(MatchType matchType) {
		setMatchType(matchType);
	}
	/** Returns the match type. **/
	public MatchType getMatchType() {
		return this.matchType;
	}
	/** Sets the match type. **/
	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}
}