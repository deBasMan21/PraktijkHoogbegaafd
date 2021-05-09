package nl.avans.praktijkhoogbegaafd.ui.childpage;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.ui.graph.GraphViewModel;

public class ChildPage extends Fragment {

    private ChildPageViewModel mViewModel;

    public static ChildPage newInstance() {
        return new ChildPage();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new ViewModelProvider(this).get(ChildPageViewModel.class);

        View root = inflater.inflate(R.layout.child_page_fragment, container, false);

        Button button = root.findViewById(R.id.bn_child_page_graph);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToGraph();
            }
        });


        return root;
    }

    public void navigateToGraph(){
        NavHostFragment.findNavController(this).navigate(R.id.nav_home);
    }


}