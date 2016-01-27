package com.pyamsoft.powermanager.ui.grid;

import com.pyamsoft.powermanager.ui.detail.DetailBaseFragment;
import java.util.List;

public interface GridInterface {

  void onItemClicked(final DetailBaseFragment fragment);

  void onItemMoved(final int fromPosition, final int toPosition);

  List<String> getItems();
}
