package org.gospelcoding.biblehead;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import android.widget.ArrayAdapter;
        import android.widget.TextView;

        import java.util.List;

public class VerseArrayAdapter extends ArrayAdapter<Verse> {

    Context context;
    List<Verse> verses;

    public VerseArrayAdapter(Context context, List<Verse> verses){
        super(context, -1, verses);

        this.context = context;
        this.verses = verses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Verse verse = verses.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View verseView = inflater.inflate(R.layout.verse_list_item, parent, false);
        ((TextView) verseView.findViewById(R.id.verse_reference)).setText(verse.getReference());

        return verseView;
    }
}