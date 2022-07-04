@extends('install.layout.master')
@section('title')
Installation
@endsection
@section('thankyou')
<style>
    .main-col {
        display: none !important;
    }
</style>
<div class="col-lg-4 col-lg-offset-4 mt-5">
    <div class="thankyou-box">
        <h2> Solution for FPOs 🤟</h2>
        <p>Designed by Arshdeep Singh <a
                href="https://megafpo.com" target="_blank">MegaFPO</a></p>
        <a href="{{ url('install/pre-installation') }}" class="btn btn-primary" style="margin-top: 2rem;">Let's Go</a>
    </div>
</div>
@endsection