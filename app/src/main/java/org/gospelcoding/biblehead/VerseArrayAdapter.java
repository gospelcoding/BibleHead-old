package org.gospelcoding.biblehead;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.TextView;

        import java.util.List;

public class VerseArrayAdapter extends ArrayAdapter<Verse> {

    Context context;

    public VerseArrayAdapter(Context context, List<Verse> verses){
        super(context, -1, verses);

        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Verse verse = getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View verseView = inflater.inflate(R.layout.verse_list_item, parent, false);
        ((TextView) verseView.findViewById(R.id.verse_reference)).setText(verse.getReference());

        Button learned = ((Button) verseView.findViewById(R.id.learn_verse));
        if (verse.learned)
            learned.setVisibility(View.INVISIBLE);
        else
            learned.setTag(verse.id);

        return verseView;
    }

    public void insert(Verse verse){
        int i = 0;
        while (i<getCount() && verse.comesAfter(getItem(i)))
            ++i;
        insert(verse, i);
    }

    public void markLearned(int verseId){
        int i=0;
        while(i<getCount() && getItem(i).id != verseId)
            ++i;
        getItem(i).learned = true;
        notifyDataSetChanged();
    }
}