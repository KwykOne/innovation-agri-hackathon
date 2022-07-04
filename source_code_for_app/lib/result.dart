import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';

class MyResults extends StatelessWidget {
  Uint8List bytes;
  final String rank;
  final String shelflife;

  MyResults({required this.bytes, required this.rank, required this.shelflife});

  @override
  Widget build(BuildContext context) {
    return Container(
        child: Column(
      children: [
        SizedBox(
          height: MediaQuery.of(context).size.width,
          width: MediaQuery.of(context).size.width,
          child: Image.memory(bytes),
        ),
        SizedBox(height: 20),
        Text(rank),
        SizedBox(height: 20),
        Text(shelflife)
      ],
    ));
  }
}
