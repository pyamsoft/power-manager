/*
 * Copyright 2017 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.powermanager.manage

import android.content.Context
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.CheckResult
import android.support.annotation.LayoutRes
import android.support.annotation.Px
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.annotation.StyleRes
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.pyamsoft.powermanager.R
import com.pyamsoft.powermanager.databinding.ViewExpanderBinding
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderHelper
import timber.log.Timber

class ExpanderView : FrameLayout {

  private var expanded: Boolean = false
  private var arrowLoad = LoaderHelper.empty()
  private var arrowAnimation: ViewPropertyAnimatorCompat? = null
  private var containerAnimation: ViewPropertyAnimatorCompat? = null
  private lateinit var binding: ViewExpanderBinding

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context,
      attrs, defStyleAttr) {
    init()
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) constructor(context: Context,
      attrs: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context,
      attrs, defStyleAttr, defStyleRes) {
    init()
  }

  private fun init() {
    if (isInEditMode) {
      Timber.d("In edit mode!")
      addView(LinearLayout(context))
      return
    }

    binding = ViewExpanderBinding.inflate(LayoutInflater.from(context), this, false)
    addView(binding.root)

    cancelArrowAnimation()
    cancelContainerAnimation()
    if (expanded) {
      ViewCompat.setRotation(binding.expanderArrow, 180f)
      binding.expanderContainer.alpha = 1F
      //binding.expanderContainer.setScaleY(1);
      binding.expanderContainer.visibility = View.VISIBLE
    } else {
      ViewCompat.setRotation(binding.expanderArrow, 0f)
      binding.expanderContainer.visibility = View.GONE
      binding.expanderContainer.alpha = 0F
      //binding.expanderContainer.setScaleY(0);
    }

    binding.expanderContainer.visibility = if (expanded) View.VISIBLE else View.GONE
    binding.expanderTitleContainer.setOnClickListener {
      expanded = !expanded
      cancelArrowAnimation()
      arrowAnimation = ViewCompat.animate(binding.expanderArrow).rotation(
          if (expanded) 180F else 0F)
      arrowAnimation!!.start()

      cancelContainerAnimation()
      if (expanded) {
        // This is expanding now
        // Be visible, but hidden
        binding.expanderContainer.alpha = 0F

        // TODO Animation is buggy
        //binding.expanderContainer.setScaleY(0);
        containerAnimation = ViewCompat.animate(binding.expanderContainer).alpha(1F).setListener(
            object : ViewPropertyAnimatorListenerAdapter() {
              override fun onAnimationStart(view: View?) {
                view!!.visibility = View.VISIBLE
              }

              override fun onAnimationEnd(view: View?) {
                view!!.visibility = View.VISIBLE
              }
            })
        containerAnimation!!.start()
      } else {
        // This is collapsing now
        // Be visible
        binding.expanderContainer.alpha = 1F

        // TODO Animation is buggy
        //binding.expanderContainer.setScaleY(1);
        containerAnimation = ViewCompat.animate(binding.expanderContainer).alpha(0F).setListener(
            object : ViewPropertyAnimatorListenerAdapter() {
              override fun onAnimationStart(view: View?) {
                view!!.visibility = View.VISIBLE
              }

              override fun onAnimationEnd(view: View?) {
                view!!.visibility = View.GONE
              }
            })
        containerAnimation!!.start()
      }
    }
  }

  internal fun cancelArrowAnimation() {
    if (arrowAnimation != null) {
      arrowAnimation!!.cancel()
      arrowAnimation = null
    }
  }

  internal fun cancelContainerAnimation() {
    if (containerAnimation != null) {
      containerAnimation!!.cancel()
      containerAnimation = null
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (isInEditMode) {
      Timber.d("In edit mode!")
      return
    }

    arrowLoad = LoaderHelper.unload(arrowLoad)
    arrowLoad = ImageLoader.fromResource(context, R.drawable.ic_arrow_down_24dp).into(
        binding.expanderArrow)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    if (isInEditMode) {
      Timber.d("In edit mode!")
      return
    }

    arrowLoad = LoaderHelper.unload(arrowLoad)
    cancelArrowAnimation()
    cancelContainerAnimation()
  }

  @CheckResult fun editTitleView(): TextView {
    return binding.expanderTitle
  }

  fun setTitle(title: String) {
    setTitle(SpannableString(title))
  }

  fun setTitle(@StringRes title: Int) {
    setTitle(SpannableString(context.getString(title)))
  }

  fun setTitle(title: Spannable) {
    binding.expanderTitle.text = title
    binding.expanderTitle.visibility = View.VISIBLE
  }

  fun clearTitle() {
    binding.expanderTitle.text = null
    binding.expanderTitle.visibility = View.GONE
  }

  fun setTitleTextSize(@Px size: Int) {
    binding.expanderTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
  }

  @CheckResult fun editDescriptionView(): TextView {
    return binding.expanderDescription
  }

  fun setDescription(description: String) {
    setDescription(SpannableString(description))
  }

  fun setDescription(@StringRes description: Int) {
    setDescription(SpannableString(context.getString(description)))
  }

  fun setDescription(description: Spannable) {
    binding.expanderDescription.text = description
    binding.expanderDescription.visibility = View.VISIBLE
  }

  fun clearDescription() {
    binding.expanderDescription.text = null
    binding.expanderDescription.visibility = View.GONE
  }

  fun setExpandingContent(@LayoutRes layout: Int) {
    setExpandingContent(LayoutInflater.from(context).inflate(layout, this, false))
  }

  fun setExpandingContent(view: View) {
    binding.expanderContainer.addView(view)
  }
}
