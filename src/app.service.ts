import { Injectable } from '@nestjs/common';

@Injectable()
export class AppService {
  getHello(): string {
    return 'Hello World!';
  }

  add(a: number, b: number): number {
    return a + b;
  }
  multiply(a: number, b: number): number {
  return a * b;
}
}