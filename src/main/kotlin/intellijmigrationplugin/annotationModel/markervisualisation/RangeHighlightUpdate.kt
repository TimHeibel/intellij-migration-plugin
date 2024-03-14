package intellijmigrationplugin.annotationModel.markervisualisation

class RangeHighlightUpdate {

    public var added: MutableList<HighlightAnnotationSnippet>
    public var removed: MutableList<HighlightAnnotationSnippet>
    public var changed: MutableList<HighlightAnnotationSnippet>

    constructor(added: MutableList<HighlightAnnotationSnippet>,
                removed: MutableList<HighlightAnnotationSnippet>,
                changed: MutableList<HighlightAnnotationSnippet>) {
        this.added = added
        this.removed = removed
        this.changed = changed
    }

}