@extends('admin.layouts.master')
@section("title") Edit Store - Dashboard
@endsection
@section('content')
<div class="page-header">
    <div class="page-header-content header-elements-md-inline">
        <div class="page-title d-flex">
            <h4>
                <span class="font-weight-bold mr-2">Editing</span>
                <i class="icon-circle-right2 mr-2"></i>
                <span class="font-weight-bold mr-2">{{ $restaurant->name }}</span>
            </h4>
            <a href="#" class="header-elements-toggle text-default d-md-none"><i class="icon-more"></i></a>
        </div>
    </div>
</div>

<div class="content">
    <div class="col-md-12">
        <div class="card">
            <div class="card-body" style="min-height: 75vh;">
                <form action="{{ route('admin.updateRestaurant') }}" method="POST" enctype="multipart/form-data" id="storeMainForm" style="min-height: 75vh;">
                    @csrf
                    <input type="hidden" name="window_redirect_hash" value="">
                    <input type="hidden" name="id" value="{{ $restaurant->id }}">

                    <div class="text-right">
                        <button type="submit" class="btn btn-primary btn-labeled btn-labeled-left btn-lg btnUpdateStore">
                        <b><i class="icon-database-insert ml-1"></i></b>
                        Update Store
                        </button>
                    </div>

                    <div class="d-lg-flex justify-content-lg-left">
                        <ul class="nav nav-pills flex-column mr-lg-3 wmin-lg-250 mb-lg-0">
                            <li class="nav-item">
                                <a href="#generalSettings" class="nav-link active" data-toggle="tab">
                                <i class="icon-store2 mr-2"></i>
                                General
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="#metaDataSettings" class="nav-link" data-toggle="tab">
                                <i class="icon-info22 mr-2"></i>
                                Meta Data
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="#operationAreaSettings" class="nav-link" data-toggle="tab">
                                <i class="icon-map mr-2"></i>
                                Operation Area
                                </a>
                            </li>
                            
                        </ul>
                        <div class="tab-content" style="width: 100%; padding: 0 25px;">

                            <div class="tab-pane fade show active" id="generalSettings">
                                <legend class="font-weight-semibold text-uppercase font-size-sm">
                                    General Settings
                                </legend>
                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label"><span class="text-danger">*</span>Store Name:</label>
                                    <div class="col-lg-9">
                                        <input value="{{ $restaurant->name }}" type="text" class="form-control form-control-lg" name="name"
                                            placeholder="Store Name" required>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label"><span class="text-danger">*</span>Description:</label>
                                    <div class="col-lg-9">
                                        <input value="{{ $restaurant->description }}" type="text" class="form-control form-control-lg" name="description"
                                            placeholder="Store Short Description" required>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label">Image:</label>
                                    <div class="col-lg-9">
                                        <img src="{{ substr(url("/"), 0, strrpos(url("/"), '/')) }}{{ $restaurant->image }}" alt="Image" width="160" style="border-radius: 0.275rem;">
                                        <img class="slider-preview-image hidden" style="border-radius: 0.275rem;"/>
                                        <div class="uploader">
                                            <input type="hidden" name="old_image" value="{{ $restaurant->image }}">
                                            <input type="file" class="form-control-uniform" name="image" accept="image/x-png,image/gif,image/jpeg" onchange="readURL(this);">
                                            <span class="help-text text-muted">Image dimension 160x117</span>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label">Store Categories: </label>
                                    <div class="col-lg-9">
                                        <select multiple="multiple" class="form-control selectRestaurantCategory" data-fouc name="restaurant_category_restaurant[]">
                                            @foreach($restaurantCategories as $rC)
                                            <option value="{{ $rC->id }}" class="text-capitalize" {{isset($restaurant) &&  in_array($restaurant->id, $rC->restaurants()->pluck('restaurant_id')->toArray()) ? 'selected' : '' }}>{{ $rC->name }}</option>
                                            @endforeach
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label"><span class="text-danger">*</span>Store URL</label>
                                    <div class="col-lg-9">
                                        <input value="{{ $restaurant->slug }}" type="text" class="form-control form-control-lg" name="store_url"
                                            placeholder="Store URL" required>
                                            <p onclick="copyURL()" class="text-muted">https://{{ request()->getHttpHost() }}/stores/<strong><span id="storeURL">{{ $restaurant->slug }}</span></strong></p>
                                    </div>

                                </div>
                                
                            </div>

                            <div class="tab-pane fade" id="metaDataSettings">
                                <legend class="font-weight-semibold text-uppercase font-size-sm">
                                    Meta Settings
                                </legend>
                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label"><span class="text-danger">*</span>Full Address:</label>
                                    <div class="col-lg-9">
                                        <input value="{{ $restaurant->address }}" type="text" class="form-control form-control-lg" name="address"
                                            placeholder="Full Address of Store" required>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label" data-popup="tooltip" title="Pincode / Postcode / Zip Code" data-placement="bottom">Pincode:</label>
                                    <div class="col-lg-9">
                                        <input value="{{ $restaurant->pincode }}" type="text" class="form-control form-control-lg" name="pincode"
                                            placeholder="Pincode / Postcode / Zip Code of Store">
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label">Land Mark:</label>
                                    <div class="col-lg-9">
                                        <input value="{{ $restaurant->landmark }}" type="text" class="form-control form-control-lg" name="landmark"
                                            placeholder="Any Near Landmark">
                                    </div>
                                </div>
                            
                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label">Certificate/License Code:</label>
                                    <div class="col-lg-9">
                                        <input value="{{ $restaurant->certificate }}" type="text" class="form-control form-control-lg" name="certificate"
                                            placeholder="Certificate Code or License Code">
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label"><strong>Custom Store Message
                                    <i class="icon-question3 ml-1" data-popup="tooltip" title="This will be displayed above search bar on Stores page (Custom HTML can be used)" data-placement="left"></i>
                                    </strong></label>
                                    <div class="col-lg-9">
                                        <textarea class="summernote-editor" name="custom_message" placeholder="Custom Store Message - Leave empty to hide" rows="6">{{ $restaurant->custom_message }}</textarea>
                                    </div>
                                </div>
                            </div>

                            <div class="tab-pane fade" id="operationAreaSettings">
                                <legend class="font-weight-semibold text-uppercase font-size-sm">
                                    Operation Area Settings
                                </legend>
                                
                                
                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label">Latitude:</label>
                                    <div class="col-lg-9">
                                        <input type="text" class="form-control form-control-lg gllpLatitude latitude" value="{{ $restaurant->latitude }}" name="latitude" placeholder="Latitude of the Store" required="required">
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label class="col-lg-3 col-form-label">Longitude:</label>
                                    <div class="col-lg-9">
                                        <input type="text" class="form-control form-control-lg gllpLongitude longitude" value="{{ $restaurant->longitude }}" name="longitude" placeholder="Longitude of the Store" required="required">
                                    </div>
                                </div>
                            </div>
                    </div>
                    
                    <div class="text-right mt-5">
                        <button type="submit" class="btn btn-primary btn-labeled btn-labeled-left btn-lg btnUpdateStore">
                        <b><i class="icon-database-insert ml-1"></i></b>
                        Update Store
                        </button>
                    </div>
                </form>
                
            </div>
        </div>
    </div>
</div>


<script>

    function readURL(input) {
        if (input.files && input.files[0]) {
            let reader = new FileReader();
            reader.onload = function (e) {
                $('.slider-preview-image')
                    .removeClass('hidden')
                    .attr('src', e.target.result)
                    .width(160)
                   .height(117)
                   .css('borderRadius', '0.275rem');
            };
            reader.readAsDataURL(input.files[0]);
        }
    }
    
    function add(data) {
        var para = document.createElement("div");
        let day = data.getAttribute("data-day")
        para.innerHTML ="<div class='form-group row'> <div class='col-lg-5'><label class='col-form-label'>Opening Time</label><input type='text' class='form-control clock form-control-lg' name='"+day+"[]' required> </div> <div class='col-lg-5'> <label class='col-form-label'>Closing Time</label><input type='text' class='form-control clock form-control-lg' name='"+day+"[]'  required> </div> <div class='col-lg-2'> <label class='col-form-label text-center' style='width: 43px'></span><i class='icon-circle-down2'></i></label><br><button class='remove btn btn-danger' data-popup='tooltip' data-placement='right' title='Remove Time Slot'><i class='icon-cross2'></i></button></div></div>";
        document.getElementById(day).appendChild(para);
        $('.clock').bootstrapMaterialDatePicker({
            shortTime: true,
            date: false,
            time: true,
            format: 'HH:mm'
        });
    }
    
    $(function () {
        
        $('input[name=store_url]').keyup(function(event) {
            let slug = $(this).val();
            slug = slug.toLowerCase();
            slug = slug.replace(/[^a-zA-Z0-9]+/g,'-');
            $(this).val(slug);
            $('#storeURL').html(slug);
        });

        $('body').tooltip({
            selector: 'button'
        });
    
        $('.clock').bootstrapMaterialDatePicker({
            shortTime: true,
            date: false,
            time: true,
            format: 'HH:mm'
        });
        $(document).on("click", ".remove", function() {
            $(this).tooltip('hide')
            $(this).parent().parent().remove();
        });
    
        $('.select').select2({
            minimumResultsForSearch: Infinity,
        });
        
        $('.selectRestaurantCategory').select2({
            closeOnSelect: false
        })
    
      if (Array.prototype.forEach) {
               var elems = Array.prototype.slice.call(document.querySelectorAll('.switchery-primary'));
               elems.forEach(function(html) {
                   var switchery = new Switchery(html, { color: '#2196F3' });
               });
           }
           else {
               var elems = document.querySelectorAll('.switchery-primary');
               for (var i = 0; i < elems.length; i++) {
                   var switchery = new Switchery(elems[i], { color: '#2196F3' });
               }
           }
    
       $('.form-control-uniform').uniform();
    
       $('.rating').numeric({allowThouSep:false,  min: 1, max: 5, maxDecimalPlaces: 1 });
       $('.delivery_time').numeric({allowThouSep:false});
       $('.price_range').numeric({allowThouSep:false});
       $('.latitude').numeric({allowThouSep:false});
       $('.longitude').numeric({allowThouSep:false});
       $('.restaurant_charges').numeric({ allowThouSep:false, maxDecimalPlaces: 2 });
       $('.delivery_charges').numeric({ allowThouSep:false, maxDecimalPlaces: 2 });
       $('.commission_rate').numeric({ allowThouSep:false, maxDecimalPlaces: 2, max: 100 });
    
       $('.base_delivery_charge').numeric({ allowThouSep:false, maxDecimalPlaces: 2, allowMinus: false });
        $('.base_delivery_distance').numeric({ allowThouSep:false, maxDecimalPlaces: 0, allowMinus: false });
        $('.extra_delivery_charge').numeric({ allowThouSep:false, maxDecimalPlaces: 2, allowMinus: false });
        $('.extra_delivery_distance').numeric({ allowThouSep:false, maxDecimalPlaces: 0, allowMinus: false });
        
        $('.min_order_price').numeric({ allowThouSep:false, maxDecimalPlaces: 2, allowMinus: false });
        
    
        @if($restaurant->delivery_charge_type == "FIXED")
            $('#dynamicChargeDiv').addClass('hidden');
        @else
            $('#deliveryCharge').addClass('hidden');
        @endif
       
        $("[name='delivery_charge_type']").change(function(event) {
             if ($(this).val() == "FIXED") {
                 $('#dynamicChargeDiv').addClass('hidden');
                 $('#deliveryCharge').removeClass('hidden')
             } else {
                 $('#deliveryCharge').addClass('hidden');
                 $('#dynamicChargeDiv').removeClass('hidden')
             }
         });

        $('#schedulingSettings').click(function(event) {
            var targetOffset = $('#autoSchedulingBlock').offset().top - 70;
            $('html, body').animate({scrollTop: targetOffset}, 500);
        });

        $('#payoutDetails').click(function(event) {
            var targetOffset = $('#payoutDetailsBlock').offset().top - 70;
            $('html, body').animate({scrollTop: targetOffset}, 500);
        });
   

        $('.summernote-editor').summernote({
           height: 200,
           popover: {
               image: [],
               link: [],
               air: []
             }
        });

        /* Navigate with hash */
        var hash = window.location.hash;
        $("[name='window_redirect_hash']").val(hash);
        hash && $('ul.nav a[href="' + hash + '"]').tab('show');
        $('.nav-pills a').click(function (e) {
            $(this).tab('show');
            var scrollmem = $('body').scrollTop();
            window.location.hash = this.hash;
            $("[name='window_redirect_hash']").val(this.hash);
            $('html, body').scrollTop(scrollmem);
        });

        $('.btnUpdateStore').click(function () {
            $('input:invalid').each(function () {
                // Find the tab-pane that this element is inside, and get the id
                var $closest = $(this).closest('.tab-pane');
                var id = $closest.attr('id');

                // Find the link that corresponds to the pane and have it show
                $('ul.nav a[href="#' + id + '"]').tab('show');

                var hash = '#'+id;
                window.location.hash = hash;
                $("[name='window_redirect_hash']").val(hash);

                return false;
            });
        });

     });
</script>
@endsection