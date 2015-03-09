// Generated code from Butter Knife. Do not modify!
package com.example.christofer.test2;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final com.example.christofer.test2.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131230783, "field 'text'");
    target.text = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131230784, "field 'greetingButton' and method 'sayHi'");
    target.greetingButton = (android.widget.Button) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sayHi((android.widget.Button) p0);
        }
      });
    view = finder.findRequiredView(source, 2131230785, "field 'byeButton' and method 'sayBye'");
    target.byeButton = (android.widget.Button) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.sayBye((android.widget.Button) p0);
        }
      });
    view = finder.findRequiredView(source, 2131230786, "field 'posButton'");
    target.posButton = (android.widget.Button) view;
    view = finder.findRequiredView(source, 2131230789, "field 'addButton' and method 'add'");
    target.addButton = (android.widget.Button) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.add();
        }
      });
    view = finder.findRequiredView(source, 2131230788, "field 'mulTimes'");
    target.mulTimes = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131230790, "field 'mulMessage'");
    target.mulMessage = (android.widget.TextView) view;
  }

  public static void reset(com.example.christofer.test2.MainActivity target) {
    target.text = null;
    target.greetingButton = null;
    target.byeButton = null;
    target.posButton = null;
    target.addButton = null;
    target.mulTimes = null;
    target.mulMessage = null;
  }
}
