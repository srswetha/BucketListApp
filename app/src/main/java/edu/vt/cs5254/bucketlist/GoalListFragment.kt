package edu.vt.cs5254.bucketlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.bucketlist.databinding.FragmentGoalListBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GoalListFragment : Fragment() {

    private var _binding: FragmentGoalListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {"FragmentGoalListBinding is Null!!"}


    private  val vm: GoalListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().addMenuProvider(object  : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_goal_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId){
                    R.id.new_goal -> {
                        showNewGoal()
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner)

        _binding = FragmentGoalListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getItemTouchHelper().attachToRecyclerView(binding.goalRecyclerView)
        binding.goalRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.noGoalButton.setOnClickListener {
            showNewGoal()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.goals.collect { goals ->
                    Log.d("GoalListFragment", "Collected goals: $goals")
                    val sortedGoals = goals.sortedByDescending { it.lastUpdated }

                    if (sortedGoals.isEmpty()) {
                        binding.goalRecyclerView.visibility = View.GONE
                        binding.noGoalText.visibility = View.VISIBLE
                        binding.noGoalButton.visibility = View.VISIBLE
                    } else {
                        binding.goalRecyclerView.visibility = View.VISIBLE
                        binding.noGoalText.visibility = View.GONE
                        binding.noGoalButton.visibility = View.GONE
                    }

                    binding.goalRecyclerView.adapter = GoalListAdapter(sortedGoals) { goalId ->
                        findNavController().navigate(GoalListFragmentDirections.showDetail(goalId))
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showNewGoal(){

        viewLifecycleOwner.lifecycleScope.launch {
            val goal = Goal()
            vm.addGoal(goal)

            findNavController().navigate(GoalListFragmentDirections.showDetail(goal.id))
        }



    }

    private fun getItemTouchHelper(): ItemTouchHelper{
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean  = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val goalHolder = viewHolder as GoalHolder
                val swipedGoal = goalHolder.boundGoal
                vm.deleteGoal(swipedGoal)
            }

        })
    }

}