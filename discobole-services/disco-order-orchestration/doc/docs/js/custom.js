// SPDX-FileCopyrightText: 2025, 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

(function($) {

  // Navigation scrolls
  $('.navbar-nav li a').bind('click', function(event) {
    $('.navbar-nav li').removeClass('active');
    $(this).closest('li').addClass('active');
  });

  $(".navbar-collapse a").on('click', function() {
    $(".navbar-collapse.collapse").removeClass('in');
  });

  // Video controls
  $("video").on('click', function() {
    if (this.paused) {
      this.play();
    } else {
      this.pause();
    }
  });

})(jQuery);
