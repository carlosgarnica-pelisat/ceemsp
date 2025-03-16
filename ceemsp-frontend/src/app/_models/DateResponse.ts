import {Deserializable} from "./Deserializable";

export default class DateResponse implements Deserializable {
  date: string;

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }
}
