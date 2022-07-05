@extends('admin.layouts.master')
@section("title") Stores - Dashboard
@endsection
@section('content')
<style>
    .delivery-div {
        background-color: #fafafa;
        padding: 1rem;
    }

    .location-search-block {
        position: relative;
        top: -26rem;
        z-index: 999;
    }
</style>
<div class="page-header">
    <div class="page-header-content header-elements-md-inline">
        <div class="page-title d-flex">
            <h4>
                @if(empty($query))
                <span class="font-weight-bold mr-2">TOTAL</span>
                <i class="icon-circle-right2 mr-2"></i>
                <span class="font-weight-bold mr-2">{{ $count }} Stores</span>
                @else
                <span class="font-weight-bold mr-2">TOTAL</span>
                <i class="icon-circle-right2 mr-2"></i>
                <span class="font-weight-bold mr-2">{{ $count }} Stores</span>
                <br>
                <span class="font-weight-normal mr-2">Showing results for "{{ $query }}"</span>
                @endif
            </h4>
            <a href="#" class="header-elements-toggle text-default d-md-none"><i class="icon-more"></i></a>
        </div>
        <div class="header-elements d-none py-0 mb-3 mb-md-0">
            <div class="breadcrumb">
                <a href="{{ route('admin.sortStores') }}"
                    class="btn btn-secondary btn-labeled btn-labeled-left mr-2">
                    <b><i class="icon-sort"></i></b>
                    Sort Stores
                </a>
                @if(!Request::is('admin/stores/pending-acceptance'))
                <a href="{{ route('admin.pendingAcceptance') }}"
                    class="btn btn-secondary btn-labeled btn-labeled-left mr-2">
                    <b><i class="icon-exclamation"></i></b>
                    Pending Approval Stores
                </a>
                @endif
                @if(Request::is('admin/stores'))
                <button type="button" class="btn btn-secondary btn-labeled btn-labeled-left mr-2" id="addNewRestaurant"
                    data-toggle="modal" data-target="#addNewRestaurantModal">
                    <b><i class="icon-plus2"></i></b>
                    Add New Store
                </button>
                <button type="button" class="btn btn-secondary btn-labeled btn-labeled-left" id="addBulkRestaurant"
                    data-toggle="modal" data-target="#addBulkRestaurantModal">
                    <b><i class="icon-database-insert"></i></b>
                    Bulk CSV Upload
                </button>
                @endif
            </div>
        </div>
    </div>
</div>
<div class="content">
    <form action="{{ route('admin.post.searchRestaurants') }}" method="GET">
        <div class="form-group form-group-feedback form-group-feedback-right search-box">
            <input type="text" class="form-control form-control-lg search-input" placeholder="Search with store name"
                name="query">
            <div class="form-control-feedback form-control-feedback-lg">
                <i class="icon-search4"></i>
            </div>
        </div>
        <button type="submit" class="hidden">Search</button>
        @csrf
    </form>
    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                        <tr>
                            <th style="width: 5%;">ID</th>
                            <th style="width: 10%">Image</th>
                            <th style="width: 15%">Name</th>
                            <th style="width: 15%">Owner</th>
                            @if($dapCheck)
                            <th>Operational Areas</th>
                            @endif
                            <th style="width: 15%">Created At</th>
                            <th class="text-center" style="width: 10%;"><i class="
                                icon-circle-down2"></i></th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach ($restaurants as $restaurant)
                        <tr>
                            <td>{{ $restaurant->id }}</td>
                            <td><img src="{{substr(url("/"), 0, strrpos(url("/"), '/'))}}{{ $restaurant->image }}"
                                    alt="{{ $restaurant->name }}" height="80" width="80"
                                    style="border-radius: 0.275rem;"></td>
                            <td>{{ $restaurant->name }}</td>
                            <td>
                            @if(count($restaurant->users))
                            @php
                                $resUsercount = 0
                            @endphp
                                @foreach($restaurant->users as $restaurantUser)
                                    @if($restaurantUser->hasRole("Store Owner"))
                                    @php
                                        $resUsercount++;
                                    @endphp
                                        <div>
                                            <a href="{{ route('admin.get.editUser', $restaurantUser->id) }}">{{ $restaurantUser->name }}</a>
                                            <a href="{{ route('admin.impersonate', $restaurantUser->id) }}" class="btn btn-sm btn-default" data-popup="tooltip"
                                            data-placement="left" title="Login as {{ $restaurantUser->name }}"> <i class="icon-circle-right2 text-warning"></i></a>
                                        </div>
                                    @endif
                                @endforeach
                            @if($resUsercount == 0)
                                <span class="badge badge-flat border-grey-800 text-default text-capitalize">
                                    UNASSIGNED
                                </span>
                            @endif
                            @else
                                <span class="badge badge-flat border-grey-800 text-default text-capitalize">
                                    UNASSIGNED
                                </span>
                            @endif
                            </td>
                            @if($dapCheck)
                            <td>
                            
                            @if(count($restaurant->delivery_areas) === 0)
                              <span class="btn btn-xs btn-secondary mr-1 mb-1 p-1" style="font-size: 0.9rem;">  NA </span>
                            @else
                                @foreach($restaurant->delivery_areas as $deliveryArea)
                                <span class="btn btn-xs btn-secondary mr-1 mb-1 p-1" style="font-size: 0.9rem;"> <small>{{ $deliveryArea->name }} </small></span>
                                @endforeach
                            @endif
                            </td>
                            @endif
                            <td>{{ $restaurant->created_at->diffForHumans() }}</td>
                            <td class="text-center">
                                <div class="btn-group btn-group-justified align-items-center">
                                    <a href="{{ route('admin.get.editRestaurant', $restaurant->id) }}"
                                        class="btn btn-sm btn-primary"> Edit </a>
                                     <a href="{{ route('admin.getRestaurantItems', $restaurant->id) }}"
                                        class="bbtn btn-sm btn-primary ml-1" data-popup="tooltip"
                                        title="View {{ $restaurant->name }}'s Items" data-placement="bottom"> <i
                                            class="icon-arrow-right5"></i> </a>

                                    <div class="checkbox checkbox-switchery ml-1" style="padding-top: 0.8rem;">
                                        <label>
                                        <input value="true" type="checkbox" class="action-switch"
                                        @if($restaurant->is_active) checked="checked" @endif data-id="{{ $restaurant->id }}">
                                        </label>
                                    </div>

                                    @if(Request::is('admin/stores/pending-acceptance'))
                                    <a href="{{ route('admin.acceptRestaurant', $restaurant->id) }}"
                                        class="badge badge-primary badge-icon ml-1" data-popup="tooltip" title="Accept"
                                        data-placement="bottom" style="background-color: #FF5722"> <i
                                            class="icon-check"></i> </a>
                                    @endif
                                </div>
                            </td>
                        </tr>
                        @endforeach
                    </tbody>
                </table>
                <div class="mt-3">
                    {{ $restaurants->appends($_GET)->links() }}
                </div>
            </div>
        </div>
    </div>
</div>
<div id="addNewRestaurantModal" class="modal fade" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><span class="font-weight-bold">Add New Store</span></h5>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form action="{{ route('admin.saveNewRestaurant') }}" method="POST" enctype="multipart/form-data">
                    <div class="form-group row">
                        <label class="col-lg-3 col-form-label"><span class="text-danger">*</span>Store Name:</label>
                        <div class="col-lg-9">
                            <input type="text" class="form-control form-control-lg" name="name" placeholder="Store Name"
                                required>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-3 col-form-label"><span class="text-danger">*</span>Description:</label>
                        <div class="col-lg-9">
                            <input type="text" class="form-control form-control-lg" name="description"
                                placeholder="Store Short Description" required>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-3 col-form-label"><span class="text-danger">*</span>Image:</label>
                        <div class="col-lg-9">
                            <img class="slider-preview-image hidden" />
                            <div class="uploader">
                                <input type="file" class="form-control-lg form-control-uniform" name="image" required
                                    accept="image/x-png,image/gif,image/jpeg" onchange="readURL(this);">
                                <span class="help-text text-muted">Image dimension 160x117</span>
                            </div>
                        </div>
                    </div>
                    
                    <hr>
                    <div class="form-group row">
                        <label class="col-lg-3 col-form-label"><span class="text-danger">*</span>Full Address:</label>
                        <div class="col-lg-9">
                            <input type="text" class="form-control form-control-lg" name="address"
                                placeholder="Full Address of Store" required>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-3 col-form-label" data-popup="tooltip"
                            title="Pincode / Postcode / Zip Code" data-placement="bottom">Pincode:</label>
                        <div class="col-lg-9">
                            <input type="text" class="form-control form-control-lg" name="pincode"
                                placeholder="Pincode / Postcode / Zip Code of Store">
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-lg-3 col-form-label">Land Mark:</label>
                        <div class="col-lg-9">
                            <input type="text" class="form-control form-control-lg" name="landmark"
                                placeholder="Any Near Landmark">
                        </div>
                    </div>
                    <fieldset class="gllpLatlonPicker">
                        {{-- <div width="100%" id="map" class="gllpMap" style="position: relative; overflow: hidden;"></div> --}}
                        <div class="form-group row">
                            <div class="col-lg-6">
                                <label class="col-form-label">Latitude:</label><input type="text"
                                    class="form-control form-control-lg gllpLatitude latitude" value="40.6976701"
                                    name="latitude" placeholder="Latitude of the Store" required="required">
                            </div>
                            <div class="col-lg-6">
                                <label class="col-form-label">Longitude:</label><input type="text"
                                    class="form-control form-control-lg gllpLongitude longitude" value="-74.2598672"
                                    name="longitude" placeholder="Longitude of the Store" required="required">
                            </div>
                        </div>
                        {{-- <input type="hidden" class="gllpZoom" value="20">
                        <div class="d-flex justify-content-center">
                            <div class="col-lg-6 d-flex location-search-block">       
                                <input type="text" class="form-control form-control-lg gllpSearchField" placeholder="Search for resraurant, city or town...">
                                <button type="button" class="btn btn-primary gllpSearchButton">Search</button>
                            </div>
                        </div> --}}
                        <span class="text-muted">You can use services like: <a
                                href="https://www.mapcoordinates.net/en" target="_blank">https://www.mapcoordinates.net/en</a></span>
                        <br> If you enter an invalid Latitude/Longitude the map system might crash with a white screen.
                    </fieldset>
                    <hr>
                    <div class="form-group row">
                        <label class="col-lg-3 col-form-label">Certificate/License Code:</label>
                        <div class="col-lg-9">
                            <input type="text" class="form-control form-control-lg" name="certificate"
                                placeholder="Certificate Code or License Code">
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-lg-3 col-form-label">Phone 1 :</label>
                        <div class="col-lg-7">
                            <input type="text" class="form-control form-control-lg" name="phone1"
                                placeholder="Phone 1">
                        </div>
                        <div class="col-lg-2">
                            <div class="checkbox checkbox-switchery mt-2">
                                <label>
                                <input value="true" type="checkbox" class="switchery-primary" checked="checked" name="enable_phone1">
                                </label>
                            </div>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-lg-3 col-form-label">Phone 2 :</label>
                        <div class="col-lg-7">
                            <input type="text" class="form-control form-control-lg" name="phone2"
                                placeholder="Phone 2">
                        </div>
                        <div class="col-lg-2">
                            <div class="checkbox checkbox-switchery mt-2">
                                <label>
                                <input value="true" type="checkbox" class="switchery-primary" checked="checked" name="enable_phone2">
                                </label>
                            </div>
                        </div>
                        </div>
                    @csrf
                    <div class="text-right">
                        <button type="submit" class="btn btn-primary">
                            SAVE
                            <i class="icon-database-insert ml-1"></i></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</div>
<div id="addBulkRestaurantModal" class="modal fade mt-5" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><span class="font-weight-bold">CSV Bulk Upload for Stores</span></h5>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form action="{{ route('admin.restaurantBulkUpload') }}" method="POST" enctype="multipart/form-data">
                    <div class="form-group row">
                        <label class="col-lg-2 col-form-label">CSV File: </label>
                        <div class="col-lg-10">
                            <div class="uploader">
                                <input type="file" accept=".csv" name="restaurant_csv"
                                    class="form-control-uniform form-control-lg" required>
                            </div>
                        </div>
                    </div>
                    <div class="text-left">
                        <button type="button" class="btn btn-primary" id="downloadSampleRestaurantCsv">
                            Download Sample CSV
                            <i class="icon-file-download ml-1"></i>
                        </button>
                    </div>
                    <div class="text-right">
                        <button type="submit" class="btn btn-primary">
                            Upload
                            <i class="icon-database-insert ml-1"></i>
                        </button>
                    </div>
                    @csrf
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
    
    $(document).ready(function() {
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
        
        $('#downloadSampleRestaurantCsv').click(function(event) {
            event.preventDefault();
            window.location.href = "{{substr(url("/"), 0, strrpos(url("/"), '/'))}}/assets/docs/restaurants-sample-csv.csv";
        });
         
         $('.rating').numeric({allowThouSep:false,  min: 1, max: 5, maxDecimalPlaces: 1 });
         $('.delivery_time').numeric({allowThouSep:false});
         $('.price_range').numeric({allowThouSep:false});
         $('.latitude').numeric({allowThouSep:false});
         $('.longitude').numeric({allowThouSep:false});
         $('.restaurant_charges').numeric({ allowThouSep:false, maxDecimalPlaces: 2, allowMinus: false });
         $('.delivery_charges').numeric({ allowThouSep:false, maxDecimalPlaces: 2, allowMinus: false });
         $('.commission_rate').numeric({ allowThouSep:false, maxDecimalPlaces: 2, max: 100, allowMinus: false });
        
        $('.delivery_radius').numeric({ allowThouSep:false, maxDecimalPlaces: 2, allowMinus: false });
        
        $('.base_delivery_charge').numeric({ allowThouSep:false, maxDecimalPlaces: 2, allowMinus: false });
        $('.base_delivery_distance').numeric({ allowThouSep:false, maxDecimalPlaces: 0, allowMinus: false });
        $('.extra_delivery_charge').numeric({ allowThouSep:false, maxDecimalPlaces: 2, allowMinus: false });
        $('.extra_delivery_distance').numeric({ allowThouSep:false, maxDecimalPlaces: 0, allowMinus: false });

        $('.min_order_price').numeric({ allowThouSep:false, maxDecimalPlaces: 2, allowMinus: false });
        
    
         $("[name='delivery_charge_type']").change(function(event) {
             if ($(this).val() == "FIXED") {
                 $("[name='base_delivery_charge']").val(null);
                 $("[name='base_delivery_distance']").val(null);
                 $("[name='extra_delivery_charge']").val(null);
                 $("[name='extra_delivery_distance']").val(null);
                 $('#dynamicChargeDiv').addClass('hidden');
                 $('#deliveryCharge').removeClass('hidden')
             } else {
                 $("[name='delivery_charges']").val(null);
                 $('#deliveryCharge').addClass('hidden');
                 $('#dynamicChargeDiv').removeClass('hidden')
             }
         });
        
        //Switch Action Function
        if (Array.prototype.forEach) {
               var elems = Array.prototype.slice.call(document.querySelectorAll('.action-switch'));
               elems.forEach(function(html) {
                   var switchery = new Switchery(html, { color: '#8360c3' });
               });
           }
           else {
               var elems = document.querySelectorAll('.action-switch');
               for (var i = 0; i < elems.length; i++) {
                   var switchery = new Switchery(elems[i], { color: '#8360c3' });
               }
           }

         $('.action-switch').click(function(event) {
            let id = $(this).attr("data-id")
            let url = "{{ url('/admin/store/disable/') }}/"+id;
            window.location.href = url;
         });
    });
    $(function () {
        $('input[name="delivery_time_slot"]').click(function() {
            var inputValue = $(this).attr("value");
            $("#" + inputValue).toggle();
        });
    });
</script>
@endsection